package com.zephyr.util

import com.zephyr.base.log.logE

fun Throwable.logE(tag: String = "") {
    logE(tag, toLogString())
}

fun Throwable.toLogString(): String {
    return "message: ${message}\ncause: ${cause}\nstack trace: ${stackTraceToString()}"
}