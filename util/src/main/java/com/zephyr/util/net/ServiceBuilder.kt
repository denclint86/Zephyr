package com.zephyr.util.net

import com.zephyr.base.appBaseUrl
import com.zephyr.base.log.logD
import com.zephyr.base.log.logE
import com.zephyr.util.toLogString
import com.zephyr.util.toPrettyJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

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


private const val TAG = "net request"

private fun <T> Call<T>.getUrl(): String = request().url().toString()

inline fun <T> NetResult<T>.handleResult(
    crossinline onSuccess: (T?) -> Unit,
    crossinline onError: (Int?, String) -> Unit
) = when (this) {
    is Success -> onSuccess(data)
    is Error -> onError(code, msg)
}

suspend inline fun <T> NetResult<T>.handleResultSuspend(
    crossinline onSuccess: suspend (T?) -> Unit,
    crossinline onError: suspend (Int?, String) -> Unit
) = when (this) {
    is Success -> onSuccess(data)
    is Error -> onError(code, msg)
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
            logD(TAG, "[${code()}]request succeed:\n$url")
            logD(TAG, "body:\n${body().toPrettyJson()}")
            callback(Success(body()))
        } // 成功

        else -> {
            val errorBodyString = errorBody().toPrettyJson()
            logE(TAG, "[${code()}]request failed at:\n $url")
            logE(TAG, "error body:\n${errorBodyString}")
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
    logE(TAG, "failed at:\n$url")
    logE(TAG, "\nthrowable:\n$throwableString")
    callback(Error(null, throwableString))
}