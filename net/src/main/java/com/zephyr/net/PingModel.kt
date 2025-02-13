package com.zephyr.net

import com.zephyr.net.bean.NetResult.Error
import com.zephyr.net.bean.NetResult.Success
import retrofit2.Call
import retrofit2.http.GET

class PingModel(private val baseUrl: String) {
    val service: PingService by lazy {
        ServiceBuilder.createGsonRetrofit(baseUrl).create(PingService::class.java)
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

    interface PingService {
        @GET("")
        fun ping(): Call<Unit>
    }
}