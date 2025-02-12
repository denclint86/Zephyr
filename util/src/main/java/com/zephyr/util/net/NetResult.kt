package com.zephyr.util.net

/**
 * service builder 请求回调
 *
 *  @Success 包含返回体,
 *  @Error 包含状态码(网络错误时为 null)以及信息
 */
sealed class NetResult<out T> {
    class Success<out T>(val data: T?) : NetResult<T>()
    class Error(val code: Int?, val msg: String) : NetResult<Nothing>()
}