package com.zephyr.util

import com.zephyr.base.appBaseUrl
import com.zephyr.base.log.logD
import com.zephyr.base.log.logE
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
        ServiceBuilder.requestEnqueue(service.ping(),
            { _ -> callback(true) },
            { _, _ -> callback(false) })
    }
}

/**
 * 本想实现让开发者自定义请求的判断逻辑, 无奈技术有限
 *
 * 使用这个接口必须要让泛型敲定, 所以写不下去了
 */
fun interface ResponseJudge<T> {
    fun isRequestSuccess(response: Response<T>): Boolean
}

/**
 * 网络请求的封装, 含同步异步的请求方法
 *
 * baseurl: 在 application 中设置 appBaseUrl
 * enableLogger: 设置为真后可以打印每个请求的日志
 *
 * @sample PingService
 * @sample PingModel
 */
object ServiceBuilder {

    const val TAG = "ServiceBuilder"
    var enableLogger = false // 设置此值以决定是否启用日志, 若要启用, 必须要先设置 Logger 的日志等级

    private var CONNECT_TIMEOUT_SET = 7L
    private const val READ_TIMEOUT_SET = 15L

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT_SET, TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIMEOUT_SET, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        retrofitBuilder(appBaseUrl)
    }

    /**
     * 在第一次获取 retrofit 对象之前调用即可设置连接超时
     */
    fun setTimeout(time: Long) {
        CONNECT_TIMEOUT_SET = time
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
    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    /**
     * ServiceBuilder.create<LoginService>()
     */
    inline fun <reified T> create(): T = create(T::class.java)

    /**
     * 检测 url 连通性并回调一个 boolean 值
     */
    fun ping(url: String, callback: (Boolean) -> Unit) = PingModel(url).ping { callback(it) }

    /**
     * 异步方法
     *
     * 在 model 层确定 T 的类型，进一步回调给 viewModel 层
     *
     * @param onSuccess 包含返回体,
     * @param onError 包含状态码(可为2xx! 网络错误时为 null )以及信息
     */
    inline fun <reified T> requestEnqueue(
        call: Call<T>,
        crossinline onSuccess: (data: T?) -> Unit,
        crossinline onError: ((code: Int?, msg: String) -> Unit)
    ) = call.enqueue(object : Callback<T> {
        val url = call.request().url().toString()

        override fun onResponse(call: Call<T>, response: Response<T>) {
            handleResponse(response, onSuccess, onError, url)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            if (enableLogger) logE(TAG, "failed at: $url\n${t.message}\n${t.stackTrace}")
            onError(null, t.message ?: "Unknown error")
        }
    })

    /**
     * 同步方法
     *
     * 在 model 层确定 T 的类型，进一步回调给 viewModel 层
     *
     * @param onSuccess 包含返回体,
     * @param onError 包含状态码(可为2xx! 网络错误时为 null )以及信息
     */
    inline fun <reified T> requestExecute(
        call: Call<T>,
        crossinline onSuccess: (data: T?) -> Unit,
        crossinline onError: ((code: Int?, msg: String) -> Unit)
    ) {
        val url = call.request().url().toString()
        handleResponse(call.execute(), onSuccess, onError, url)
    }

    /**
     * 挂起方法
     *
     * 在 model 层确定 T 的类型，进一步回调给 viewModel 层
     *
     * @param onSuccess 包含返回体,
     * @param onError 包含状态码(可为2xx! 网络错误时为 null )以及信息
     */
    suspend inline fun <reified T> requestSuspend(
        call: Call<T>,
        crossinline onSuccess: (data: T?) -> Unit,
        crossinline onError: ((code: Int?, msg: String) -> Unit)
    ) = withContext(Dispatchers.IO) {
        val url = call.request().url().toString()
        handleResponse(call.awaitResponse(), onSuccess, onError, url)
    }

    inline fun <reified T> handleResponse(
        response: Response<T>,
        crossinline onSuccess: (data: T?) -> Unit,
        crossinline onError: ((code: Int?, msg: String) -> Unit),
        url: String
    ) = try {
        response.run {
            when {
                isSuccessful -> {
                    if (enableLogger) logD(TAG, "success: $url")
                    onSuccess(body())
                } // 成功

                else -> {
                    if (enableLogger) logE(TAG, "failed at: $url")
                    onError(code(), message() ?: "Unknown error")
                } // 其他失败情况
            }
        }
    } catch (e: Exception) {
        if (enableLogger) logE(TAG, "failed at: $url\n${e.message}\n${e.stackTrace}")
        onError(null, e.message ?: "Unknown error")
    }
}