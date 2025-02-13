package com.zephyr.log

import android.util.Log

fun Throwable.logE(tag: String = "") {
    logE(tag, toLogString())
}

fun Throwable.toLogString(): String {
    return "message: ${message}\ncause: ${cause}\nstack trace: ${stackTraceToString()}"
}

const val SECOND = 1000L

// 日志等级
var LOG_LEVEL = LogLevel.DO_NOT_LOG

// 是否启用自动删除
var CLEAN_OLD = true
var DAYS_RETAINED = 4
var HOURS_RETAINED = 0
var MINUTES_RETAINED = 0
var SECONDS_RETAINED = 0

// 写入文件的间隔
var AUTO_WRITE_TIME_INTERVAL = SECOND * 5

// 命名偏好
var FILE_HEADER = "Logger-"
var FILE_PATH = "logs"
var FILE_TYPE = ".txt"
var DATE_FORMAT = "yyyy年MM月dd日"
var LOG_HEADER = ""
var TIME_FORMAT = "MM/dd HH:mm:ss"

/**
 * 功能: 定时录入日志到本地, 在崩溃时展示崩溃原因
 */
object Logger : LoggerClass()

fun logV(tag: String = "", msg: String = "") =
    with(LogLevel.VERBOSE) {
        if (LOG_LEVEL.v <= this.v) {
            val str = LOG_HEADER + msg
            Log.v(tag, str)
            Logger.appendLog(this, tag, str)
        }
    }

fun logD(tag: String = "", msg: String = "") =
    with(LogLevel.DEBUG) {
        if (LOG_LEVEL.v <= this.v) {
            val str = LOG_HEADER + msg
            Log.d(tag, str)
            Logger.appendLog(this, tag, str)
        }
    }

fun logI(tag: String = "", msg: String = "") =
    with(LogLevel.INFO) {
        if (LOG_LEVEL.v <= this.v) {
            val str = LOG_HEADER + msg
            Log.i(tag, str)
            Logger.appendLog(this, tag, str)
        }
    }

fun logW(tag: String = "", msg: String = "") =
    with(LogLevel.WARN) {
        if (LOG_LEVEL.v <= this.v) {
            val str = LOG_HEADER + msg
            Log.w(tag, str)
            Logger.appendLog(this, tag, str)
        }
    }

fun logE(tag: String = "", msg: String = "") =
    with(LogLevel.ERROR) {
        if (LOG_LEVEL.v <= this.v) {
            val str = LOG_HEADER + msg
            Log.e(tag, str)
            Logger.appendLog(this, tag, str)
        }
    }