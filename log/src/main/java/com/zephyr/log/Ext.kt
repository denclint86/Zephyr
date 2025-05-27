package com.zephyr.log

import java.io.File
import java.io.FileWriter

/**
 * 全局 logger
 */
internal val logger by lazy {
    Logger()
}

fun setOnCaughtListener(l: ((thread: Thread, throwable: Throwable) -> Unit)?) =
    logger.setOnCaughtListener(l)

fun logV(tag: String = "", msg: String = "") = logger.log(LogLevel.VERBOSE, tag, msg)
fun logD(tag: String = "", msg: String = "") = logger.log(LogLevel.DEBUG, tag, msg)
fun logI(tag: String = "", msg: String = "") = logger.log(LogLevel.INFO, tag, msg)
fun logW(tag: String = "", msg: String = "") = logger.log(LogLevel.WARN, tag, msg)
fun logE(tag: String = "", msg: String = "") = logger.log(LogLevel.ERROR, tag, msg)

fun Throwable.logE(tag: String = "") =
    logE(tag, stackTraceToString())

internal fun File.appendString(string: String) = runCatching {
    FileWriter(this, true).use { writer ->
        writer.append(string)
    }
}

internal fun File.deleteChildIf(condition: File.() -> Boolean) = runCatching {
    listFiles()?.filter(condition)?.forEach {
        it.delete()
    }
}