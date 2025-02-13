package com.zephyr.net.bean

/**
 * 流式请求结果封装
 */
sealed class StreamNetResult<out T> {
    data object Start : StreamNetResult<Nothing>()
    data class Data<out T>(val data: T?) : StreamNetResult<T>()
    data class Error(val code: Int?, val msg: String) : StreamNetResult<Nothing>()
    data class Complete(val isSuccess: Boolean, val reason: String? = null) :
        StreamNetResult<Nothing>()
}