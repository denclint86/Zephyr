package com.zephyr.log.interfaces

internal interface IExceptionHandler {
    fun register()

    /**
     * 返回布尔表示是否处理了异常
     */
    fun setOnCaughtListener(l: ((Thread, Throwable) -> Boolean)?)
}