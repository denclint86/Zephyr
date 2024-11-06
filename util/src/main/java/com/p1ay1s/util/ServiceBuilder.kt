package com.p1ay1s.util

import com.p1ay1s.base.appBaseUrl
import com.p1ay1s.base.log.logD
import com.p1ay1s.base.log.logE
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

/**
 * 网络请求的封装
 */
object ServiceBuilder {
    private interface PingService {
        @GET("/")
        fun ping(): Call<Unit>
    }

    const val TAG = "ServiceBuilder"
    var enableLogger = false

    private var connectTimeoutSet = 7L
    private const val READ_TIMEOUT_SET = 15L

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT_SET, TimeUnit.SECONDS)
            .connectTimeout(connectTimeoutSet, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        retrofitBuilder(appBaseUrl)
    }

    /**
     * 在第一次获取 retrofit 对象之前调用即可设置
     */
    fun setTimeout(time: Long) {
        connectTimeoutSet = time
    }

    /**
     * 创建并返回一个 retrofit 实例
     */
    private fun retrofitBuilder(baseUrl: String = appBaseUrl) = Retrofit.Builder()
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
    fun ping(url: String, callback: (Boolean) -> Unit) =
        with(retrofitBuilder(url).create(PingService::class.java)) {
            requestEnqueue(this.ping(),
                {
                    callback(true)
                },
                { _, _ ->
                    callback(false)
                })
        }

    /**
     * 在 model 层确定 T 的类型，进一步回调给 viewModel 层
     *
     * @param onSuccess 包含返回体,
     * @param onError 包含状态码(可为2xx! 网络错误时为 null )以及信息
     */
    inline fun <reified T> requestEnqueue(
        call: Call<T>,
        crossinline onSuccess: (data: T) -> Unit,
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

    inline fun <reified T> requestExecute(
        call: Call<T>,
        crossinline onSuccess: (data: T) -> Unit,
        crossinline onError: ((code: Int?, msg: String) -> Unit)
    ) {
        val url = call.request().url().toString()
        handleResponse(call.execute(), onSuccess, onError, url)
    }

    /**
     * 在 model 层确定 T 的类型，进一步回调给 viewModel 层
     *
     * @param onSuccess 包含返回体,
     * @param onError 包含状态码(可为2xx! 网络错误时为 null )以及信息
     */
    suspend inline fun <reified T> requestSuspend(
        call: Call<T>,
        crossinline onSuccess: (data: T) -> Unit,
        crossinline onError: ((code: Int?, msg: String) -> Unit)
    ) = withContext(Dispatchers.IO) {
        val url = call.request().url().toString()
        handleResponse(call.awaitResponse(), onSuccess, onError, url)
    }

    inline fun <reified T> handleResponse(
        response: Response<T>,
        crossinline onSuccess: (data: T) -> Unit,
        crossinline onError: ((code: Int?, msg: String) -> Unit),
        url: String
    ) = try {
        response.run {
            when {
                isSuccessful && body() != null -> {
                    if (enableLogger) logD(TAG, "success: $url")
                    onSuccess(body()!!)
                } // 成功

                else -> {
                    if (enableLogger) logE(TAG, "failed at: $url")
                    onError(code(), message() ?: "Unknown error")
                } // 其他失败情况
            }
        }
    } catch (t: Throwable) {
        if (enableLogger) logE(TAG, "failed at: $url\n${t.message}\n${t.stackTrace}")
        onError(null, t.message ?: "Unknown error")
    }
}