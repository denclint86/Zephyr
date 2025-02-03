package com.zephyr.util.net

import com.zephyr.base.appBaseUrl
import com.zephyr.util.net.ServiceBuilder.client
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

val rawRetrofit: Retrofit by lazy {
    Retrofit.Builder()
        .baseUrl(appBaseUrl)
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
}