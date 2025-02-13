package com.zephyr.net

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


/**
 * 可以直接通过它创建简单的 service
 *
 * baseurl: 在 application 中设置 appBaseUrl
 * enableLogger: 设置为真后可以打印每个请求的日志
 *
 * @sample PingModel
 */
object ServiceBuilder {

    private var READ_TIMEOUT = 30L
    private var CONNECT_TIMEOUT = 30L

    /**
     * 返回一个 Service 代理对象
     *
     * example:
     * ServiceBuilder.create(LoginService::class.java)
     */
    @JvmName("create1")
    fun <T> create(serviceClass: Class<T>, baseUrl: String, interceptor: Interceptor? = null): T =
        createGsonRetrofit(baseUrl, interceptor).create(serviceClass)

    /**
     * ServiceBuilder.create<LoginService>()
     */
    @JvmName("create2")
    inline fun <reified T> create(baseUrl: String, interceptor: Interceptor? = null): T =
        create(T::class.java, baseUrl, interceptor)

    /**
     * 创建并返回一个 retrofit 实例
     */
    fun createGsonRetrofit(baseUrl: String, interceptor: Interceptor? = null): Retrofit =
        createRetrofit(baseUrl, GsonConverterFactory.create(), interceptor)

    fun createRawRetrofit(baseUrl: String, interceptor: Interceptor? = null) =
        createRetrofit(baseUrl, ScalarsConverterFactory.create(), interceptor)

    fun createRetrofit(
        baseUrl: String,
        factory: Converter.Factory,
        interceptor: Interceptor? = null
    ) = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(createClient(interceptor))
        .addConverterFactory(factory)
        .build()

    private fun createClient(interceptor: Interceptor? = null) = OkHttpClient.Builder()
        .apply {
            interceptor?.let { addInterceptor(it) }
        }
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .build()
}