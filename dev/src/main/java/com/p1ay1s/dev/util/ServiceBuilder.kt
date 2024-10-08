package com.p1ay1s.dev.util

import com.p1ay1s.dev.base.appBaseUrl
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

const val ON_FAILURE_CODE = -1

/**
 * 网络请求的封装
 */
object ServiceBuilder {
    private interface PingService {
        @GET("/")
        fun ping(): Call<Unit>
    }

    val TAG = this::class.simpleName!!

    const val LOADING = "loading "
    const val SUCCESS = "success "
    const val FAILED = "failed "
    const val TIME_OUT = "time out "

    private const val TIMEOUT_SET = 15L

    private val client = OkHttpClient.Builder()
        .readTimeout(TIMEOUT_SET, TimeUnit.SECONDS)
        .connectTimeout(TIMEOUT_SET, TimeUnit.SECONDS)
        .build()

    private val retrofit = retrofitBuilder(appBaseUrl)

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

    // ServiceBuilder.create<LoginService>()
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
     * @param onSuccess 包含返回体
     * @param onError 包含状态码以及信息
     */
    inline fun <reified T> requestEnqueue(
        call: Call<T>,
        crossinline onSuccess: (data: T) -> Unit,
        crossinline onError: ((code: Int, msg: String) -> Unit)
    ) = call.enqueue(object : Callback<T> {
        val url = call.request().url().toString()

        override fun onResponse(call: Call<T>, response: Response<T>) {
            with(response) {
                when {
                    isSuccessful && body() != null -> onSuccess(body()!!) // 成功

                    isSuccessful && body() == null ->
                        onError(
                            ON_FAILURE_CODE,
                            "connection timeout"
                        ) // 超时

                    else -> onError(code(), message()) // 其他失败情况
                }
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            t.printStackTrace()
            onError(ON_FAILURE_CODE, t.message ?: "Unknown error")
        }
    })

    suspend inline fun <reified T> requestExecute(
        call: Call<T>,
        crossinline onSuccess: (data: T) -> Unit,
        crossinline onError: ((code: Int, msg: String) -> Unit)
    ) = withContext(Dispatchers.IO) {
        val url = call.request().url().toString()
        try {
            with(call.awaitResponse()) {
                when {
                    isSuccessful && body() != null ->onSuccess(body()!!) // 成功

                    isSuccessful && body() == null ->
                        onError(
                            ON_FAILURE_CODE,
                            "connection timeout"
                        ) // 超时

                    else -> onError(code(), message()) // 其他失败情况
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            onError(ON_FAILURE_CODE, t.message ?: "Unknown error")
        }
    }
}