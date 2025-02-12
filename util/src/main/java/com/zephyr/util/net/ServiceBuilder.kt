package com.zephyr.util.net

import com.google.gson.Gson
import com.zephyr.base.appBaseUrl
import com.zephyr.base.extension.toLogString
import com.zephyr.base.log.logD
import com.zephyr.base.log.logE
import com.zephyr.util.net.NetResult.Error
import com.zephyr.util.net.NetResult.Success
import com.zephyr.util.toPrettyJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.BufferedReader
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

private interface PingService {
    @GET("/")
    fun ping(): Call<Unit>
}

private class PingModel(private val baseUrl: String) {
    // val service: PingService by lazy { ServiceBuilder.create(PingService::class.java) }
    val service: PingService by lazy { // 一般情况下请使用注释掉的那种用法
        ServiceBuilder.retrofitBuilder(baseUrl).create(PingService::class.java)
    }

    inline fun ping(crossinline callback: (Boolean) -> Unit) {
        requestEnqueue(service.ping()) { c ->
            when (c) {
                is Error -> {}
                is Success -> {}
            }
            callback(c is Success<*>)
        }
    }
}

/**
 * 可以直接通过它创建简单的 service
 *
 * baseurl: 在 application 中设置 appBaseUrl
 * enableLogger: 设置为真后可以打印每个请求的日志
 *
 * @sample PingService
 * @sample PingModel
 */
object ServiceBuilder {

    private const val READ_TIMEOUT = 15L
    private var CONNECT_TIMEOUT = 30L

    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    /**
     * 创建并返回一个 retrofit 实例
     */
    fun retrofitBuilder(baseUrl: String = appBaseUrl): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * 返回一个 Service 代理对象
     *
     * example:
     * ServiceBuilder.create(LoginService::class.java)
     */
    @JvmName("create1")
    fun <T> create(serviceClass: Class<T>, baseUrl: String = appBaseUrl): T =
        retrofitBuilder(baseUrl).create(serviceClass)

    /**
     * ServiceBuilder.create<LoginService>()
     */
    @JvmName("create2")
    inline fun <reified T> create(baseUrl: String = appBaseUrl): T = create(T::class.java, baseUrl)
}


const val ServiceBuilderTag = "net request"

private fun <T> Call<T>.getUrl(): String = request().url().toString()

//inline fun <T> NetResult<T>.handleResult(
//    crossinline onSuccess: (T?) -> Unit,
//    crossinline onError: (Int?, String) -> Unit
//) = when (this) {
//    is Success -> onSuccess(data)
//    is Error -> onError(code, msg)
//}
//
//suspend inline fun <T> NetResult<T>.handleResultSuspend(
//    crossinline onSuccess: suspend (T?) -> Unit,
//    crossinline onError: suspend (Int?, String) -> Unit
//) = when (this) {
//    is Success -> onSuccess(data)
//    is Error -> onError(code, msg)
//}

data class RetryConfig(
    val maxAttempts: Int = 3,           // 最大重试次数
    val initialDelayMs: Long = 1000,    // 初始延迟时间（毫秒）
    val maxDelayMs: Long = 10000,       // 最大延迟时间（毫秒）
    val factor: Double = 2.0,           // 指数退避因子
    val jitter: Double = 0.1            // 抖动因子(0.0-1.0)
) {
    fun getDelayForAttempt(attempt: Int): Long {
        // 计算指数退避时间
        val exponentialDelay = (initialDelayMs * factor.pow(attempt - 1)).toLong()
        val cappedDelay = min(exponentialDelay, maxDelayMs)

        // 添加随机抖动
        val jitterOffset = (cappedDelay * jitter * (Random.nextDouble() * 2 - 1)).toLong()
        return (cappedDelay + jitterOffset).coerceAtLeast(0)
    }
}

/**
 * 同步请求方法
 */
@JvmName("requestExecute1")
fun <T> Call<T>.requestExecute(
    callback: (NetResult<T>) -> Unit
) = try {
    val response = execute()
    handleOnResponse(response, callback)
} catch (t: Throwable) {
    handleOnFailure(t, callback)
}

/**
 * 异步请求方法
 */
@JvmName("requestEnqueue1")
fun <T> Call<T>.requestEnqueue(
    callback: (NetResult<T>) -> Unit
) = enqueue(object : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        handleOnResponse(response, callback)
    }

    override fun onFailure(call: Call<T>, throwable: Throwable) {
        handleOnFailure(throwable, callback)
    }
})

var requestShowJson = false

fun <T> Response<T>.getErrorBodyString() = errorBody()?.string() ?: ""

/**
 * 流式请求 flow
 *
 * 发送四种状态:
 * Start
 * Data: data: T?
 * Error: code: Int?, msg: String
 * Complete: reason: String?
 */
inline fun <reified T> Response<ResponseBody>.requestStream(): Flow<StreamNetResult<T>> = flow {
    emit(StreamNetResult.Start)
    try {
        if (isSuccessful) {
            val reader = body()?.byteStream()?.bufferedReader()
            if (reader != null) {
                handleStreamResponse(reader)
                return@flow
            } else {
                logE(ServiceBuilderTag, "byte stream reader is null")
                emit(StreamNetResult.Error(null, "byte stream reader is null"))
            }
        } else {
            val errorString = getErrorBodyString()
            logE(ServiceBuilderTag, errorString)
            emit(StreamNetResult.Error(code(), errorString))
        }
    } catch (t: Throwable) {
        val tString = t.toLogString()
        logE(ServiceBuilderTag, tString)
        emit(StreamNetResult.Error(null, tString))
    }
    currentCoroutineContext().ensureActive()
    emit(StreamNetResult.Complete(false))
}

suspend inline fun <reified T> FlowCollector<StreamNetResult<T>>.handleStreamResponse(reader: BufferedReader) {
    val gson = Gson()
    var isSuccess = false
    while (true) {
        val line = withContext(Dispatchers.IO) {
            reader.readLine()
        } ?: break

        when {
            line.isEmpty() || line.startsWith(":") -> continue

            line.startsWith("data: ") -> {
                val data = line.substring(6)
                if (data == "[DONE]") {
                    logE(ServiceBuilderTag, "data is \"[DONE]\"")
                    isSuccess = true
                    break
                }

                try {
                    val streamData = gson.fromJson(data, T::class.java)
                    if (requestShowJson)
                        logE(ServiceBuilderTag, streamData.toPrettyJson())
                    emit(StreamNetResult.Data(streamData))
                } catch (t: Throwable) {
                    val tString = t.toLogString()
                    logE(ServiceBuilderTag, tString)
                    emit(StreamNetResult.Error(null, "gson parse error"))
                }
            }
        }
    }
    reader.close()
    emit(StreamNetResult.Complete(isSuccess))
}


/**
 * 挂起请求方法
 */
@JvmName("requestSuspend1")
suspend fun <T> Call<T>.requestSuspend(
    callback: (NetResult<T>) -> Unit
) = withContext(Dispatchers.IO) {
    try {
        val response = awaitResponse()
        handleOnResponse(response, callback)
    } catch (t: Throwable) {
        handleOnFailure(t, callback)
    }
}

@JvmName("requestExecute2")
fun <T> requestExecute(
    call: Call<T>,
    callback: (NetResult<T>) -> Unit
) = call.requestExecute(callback)

@JvmName("requestEnqueue2")
fun <T> requestEnqueue(
    call: Call<T>,
    callback: (NetResult<T>) -> Unit
) = call.requestEnqueue(callback)

@JvmName("requestSuspend2")
suspend fun <T> requestSuspend(
    call: Call<T>,
    callback: (NetResult<T>) -> Unit
) = call.requestSuspend(callback)


private fun <T> Call<T>.handleOnResponse(
    response: Response<T>?,
    callback: (NetResult<T>) -> Unit
) = response?.run {
    val url = getUrl()
    when {
        isSuccessful -> {
            logD(ServiceBuilderTag, "[${code()}] request succeed:\n$url")
            if (requestShowJson)
                logD(ServiceBuilderTag, "body:\n${body().toPrettyJson()}")
            callback(Success(body()))
        } // 成功

        else -> {
            val errorBodyString = getErrorBodyString()
            logE(ServiceBuilderTag, "[${code()}] request failed at:\n$url")
            if (requestShowJson)
                logE(ServiceBuilderTag, "error body:\n${errorBodyString.toPrettyJson()}")
            val errorString = errorBodyString.ifBlank { message() ?: "Unknown error" }
            callback(Error(code(), errorString))
        } // 其他失败情况
    }
} ?: callback(Error(null, "response is null"))

private fun <T> Call<T>.handleOnFailure(
    throwable: Throwable?,
    callback: (NetResult<T>) -> Unit
) {
    if (throwable == null) return
    val url = getUrl()
    val throwableString = throwable.toLogString()
    logE(ServiceBuilderTag, "[] request failed at:\n$url")
    logE(ServiceBuilderTag, "\nthrowable:\n$throwableString")
    callback(Error(null, throwableString))
}