@file:Suppress("NOTHING_TO_INLINE")

package com.zephyr.base.log

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.zephyr.base.appContext
import com.zephyr.base.extension.toLogString
import com.zephyr.base.extension.toast
import com.zephyr.base.setAppContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LogLevel(val v: Int, val text: String) {
    VERBOSE(1, "Verbose"),
    DEBUG(2, "Debug"),
    INFO(3, "Information"),
    WARN(4, "Warning"),
    ERROR(5, "Error"),
    DO_NOT_LOG(6, "Do_not_log")
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

open class LoggerClass {
    protected val TAG = this::class.simpleName!!

    protected lateinit var fileDir: File
    protected lateinit var file: File

    protected var isCrashed = false

    // 当前时间的格式
    protected var accurateTimeFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())

    // 日期的格式
    protected var dateFormat: String =
        SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())

    // 文件命名格式
    protected var fileName = FILE_HEADER + dateFormat + FILE_TYPE

    protected val logBuffer = StringBuffer()

    protected val loggerScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob())
    protected var loggerJob: Job? = null

    protected val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    // 出现未被捕获的错误时的处理方式
    open val loggerCrashHandler: ((Thread, Throwable) -> Unit) = { thread, throwable ->
        isCrashed = true

        val title = thread.name
        val detail = throwable.toLogString()
        val fullMsg = "at:\n${title}\ndetails:\n$detail"

        Log.e(TAG, fullMsg)
        appendLog(LogLevel.ERROR, "UncaughtException", fullMsg)
        writeToFile()

        /**
         * 交给默认的 handler 接管
         */
        defaultHandler?.uncaughtException(thread, throwable)
    }

    /**
     * 必须在 application 中启动
     */
    fun startLogger(
        context: Application,
        logLevel: LogLevel
    ) = runCatching {
        context.setAppContext(context)
        setLogLevel(logLevel)
    }.onFailure {
        context.setAppContext(context)
        if (LOG_LEVEL.v >= LogLevel.DEBUG.v)
            "Logger 启动失败".toast()
    }

    protected fun setLogLevel(newLevel: LogLevel) =
        when (newLevel) {
            in LogLevel.VERBOSE..LogLevel.ERROR -> {
                LOG_LEVEL = newLevel
                init()
            }

            LogLevel.DO_NOT_LOG -> LOG_LEVEL = newLevel
            else -> throw Exception("log level should between VERBOSE and DO_NOT_LOG")
        }

    /**
     * 令 Logger 开始工作
     */
    protected fun init() {
        create()
        startLogCoroutine()
        registerHandler()
        cleanOldLogs()
    }


    protected fun create() {
        fileDir = File(appContext.getExternalFilesDir(null), FILE_PATH)
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        fileName = FILE_HEADER + dateFormat + FILE_TYPE
        file = File(fileDir, fileName)
    }

    protected fun startLogCoroutine() {
        if (loggerJob == null || loggerJob?.isCancelled == true) {
            loggerJob = loggerScope.launch {
                while (isActive) {
                    delay(AUTO_WRITE_TIME_INTERVAL)
                    writeToFile()
                }
            }
        }
    }

    fun stop() {
        loggerJob?.cancel()
        loggerScope.cancel()
    }

    protected fun writeToFile() {
        try {
            val file = getLogFile()
            FileWriter(file, true).use { writer ->
                writer.append(logBuffer.toString())
                logBuffer.setLength(0)
            }
        } catch (e: Exception) {
            logE(TAG, "日志写入失败")
        }
    }

    fun appendLog(level: LogLevel, tag: String, message: String) {
        val currentTime = accurateTimeFormat.format(Date())
        val logMessage = "$currentTime ${level.text} $tag\n$message\n"
        logBuffer.append(logMessage)
    }

    protected fun getLogFile(): File {
        val fileName =
            FILE_HEADER + dateFormat + FILE_TYPE

        /**
         * 判断名字主要是为了保证日期一致
         */
        if (fileName != Logger.fileName) {
            create()
            cleanOldLogs() // 日期变动再次清除日志
        }
        return file
    }

    protected fun registerHandler() {
        /**
         * 主线程捕获原理:
         * 用 handler 给主线程的 looper 添加新的任务——进入一个不断 loop 的死循环,
         * 此时原生的 loop 函数由于在内部嵌套了我们的循环,
         * 相当于被我们的 loop 接管了,
         * 而我们的 loop 被 try-catch 块包裹所以可以捕捉所有异常
         *
         * 关于 Lopper.loop()
         * 此函数在内部会获取所在线程的 looper 而不是使用 Lopper
         */
        Handler(Looper.getMainLooper()).post {
            while (!isCrashed) {
                // 当捕捉并处理异常后不再阻塞原生的 loop,
                // 以便启动崩溃详情 activity
                try {
                    Looper.loop()
                } catch (t: Throwable) {
                    loggerCrashHandler(Looper.getMainLooper().thread, t)
                }
            }
        }

        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            if (!isCrashed)
                loggerCrashHandler(t, e)
        }
    }

    protected fun cleanOldLogs() {
        if (!CLEAN_OLD) {
            logI(TAG, "未启用日志清除")
            return
        }
        val outOfDate =
            System.currentTimeMillis() - formatTime()
        fileDir.listFiles()?.forEach { file -> // 遍历路径的文件
            if (file.lastModified() < outOfDate) {
                logI(TAG, "删除过期的备份文件: ${file.name}")
                file.delete()
            }
        }
    }

    protected fun formatTime(): Long {
        return (DAYS_RETAINED * 24 * 60 * 60 + HOURS_RETAINED * 60 * 60 + MINUTES_RETAINED * 60 + SECONDS_RETAINED) * SECOND
    }
}

/**
 * 功能: 定时录入日志到本地, 在崩溃时展示崩溃原因
 */
object Logger : LoggerClass()

inline fun logV(tag: String = "", msg: String = "") =
    with(LogLevel.VERBOSE) {
        if (LOG_LEVEL.v <= this.v) {
            val str = LOG_HEADER + msg
            Log.v(tag, str)
            Logger.appendLog(this, tag, str)
        }
    }

inline fun logD(tag: String = "", msg: String = "") =
    with(LogLevel.DEBUG) {
        if (LOG_LEVEL.v <= this.v) {
            val str = LOG_HEADER + msg
            Log.d(tag, str)
            Logger.appendLog(this, tag, str)
        }
    }

inline fun logI(tag: String = "", msg: String = "") =
    with(LogLevel.INFO) {
        if (LOG_LEVEL.v <= this.v) {
            val str = LOG_HEADER + msg
            Log.i(tag, str)
            Logger.appendLog(this, tag, str)
        }
    }

inline fun logW(tag: String = "", msg: String = "") =
    with(LogLevel.WARN) {
        if (LOG_LEVEL.v <= this.v) {
            val str = LOG_HEADER + msg
            Log.w(tag, str)
            Logger.appendLog(this, tag, str)
        }
    }

inline fun logE(tag: String = "", msg: String = "") =
    with(LogLevel.ERROR) {
        if (LOG_LEVEL.v <= this.v) {
            val str = LOG_HEADER + msg
            Log.e(tag, str)
            Logger.appendLog(this, tag, str)
        }
    }