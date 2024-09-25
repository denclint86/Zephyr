@file:Suppress("NOTHING_TO_INLINE")

package com.p1ay1s.dev.base.log

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.p1ay1s.dev.base.CrashActivity
import com.p1ay1s.dev.base.appContext
import com.p1ay1s.dev.base.getFunctionName
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

// 自用日志记录
const val VERBOSE = 1
const val DEBUG = 2
const val INFO = 3
const val WARN = 4
const val ERROR = 5

const val SECOND = 1000L

// 日志等级
var LOG_LEVEL = ERROR

// 是否启用自动删除
const val CLEAN_OLD = true
const val DAYS_RETAINED = 2
const val HOURS_RETAINED = 0
const val MINUTES_RETAINED = 0
const val SECONDS_RETAINED = 0

// 写入文件的间隔
const val AUTO_WRITE_TIME_INTERVAL = SECOND * 5

// 命名偏好
const val FILE_HEADER = "Logger-"
const val FILE_PATH = "logs"
const val FILE_TYPE = ".txt"
const val DATE_FORMAT = "yyyy年MM月dd日"
const val LOG_HEADER = ""
const val TIME_FORMAT = "MM/dd HH:mm:ss"

private val levels = mapOf(
    VERBOSE to "Verbose",
    DEBUG to "Debug",
    INFO to "Information",
    WARN to "Warning",
    ERROR to "Error",
)

/**
 * 获取等级对应的字符串
 */
fun getName(level: Int): String = levels[level]!!

/**
 * 功能: 定时录入日志到本地, 在崩溃时展示崩溃原因
 */
object Logger {
    private val TAG = this::class.simpleName!!
    private lateinit var fileDir: File
    private lateinit var file: File

    private var isCrashed = false

    /**
     * crash activity 功能需要在项目中注册您继承的子类并给 crashActivity 赋值
     */
    var crashActivity: Class<out CrashActivity>? = null

    // 当前时间的格式
    private var accurateTimeFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())

    // 日期的格式
    private var dateFormat: String =
        SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())

    // 文件命名格式
    private var fileName = FILE_HEADER + dateFormat + FILE_TYPE

    private val logBuffer = StringBuffer()

    private val loggerScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var loggerJob: Job? = null

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    // 出现未被捕获的错误时的处理方式
    private val loggerCrashHandler: ((Thread, Throwable) -> Unit) = { thread, throwable ->
        isCrashed = true

        val title = thread.name
        val detail = Log.getStackTraceString(throwable)
        val fullMsg = "at: ${title}\ndetails: $detail"
        appendLog(getName(ERROR), "UncaughtException", fullMsg)
        writeToFile()

        if (crashActivity != null && appContext != null) {
            with(Intent(appContext, crashActivity)) {
                putExtra("TITLE", title)
                putExtra("DETAIL", detail)
                setFlags(FLAG_ACTIVITY_NEW_TASK) // 不按返回栈规则启动的方式
                appContext!!.startActivity(this)
            }
        } else {
            /**
             * 交给默认的 handler 接管
             */
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    /**
     * 必须在 application 中调用以更快地初始化, 否则不能保证工作
     */
    fun start(base: Context) = try {
        appContext = base
        create()
        startLogCoroutine()
        registerHandler()
        cleanOldLogs()
    } catch (_: Exception) {
        appContext = base
    }

    fun setLogLevel(newLevel: Int) {
        if (newLevel in VERBOSE..ERROR)
            LOG_LEVEL = newLevel
    }

    /**
     * 令 Logger 开始工作
     */
    private fun create() {
        if (appContext == null) return
        fileDir = File(appContext!!.getExternalFilesDir(null), FILE_PATH)
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        fileName = FILE_HEADER + dateFormat + FILE_TYPE
        file = File(fileDir, fileName)
    }

    private fun startLogCoroutine() {
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

    private fun writeToFile() {
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

    fun appendLog(level: String, tag: String, message: String) {
        val currentTime = accurateTimeFormat.format(Date())
        val logMessage = "$currentTime $level $tag\n$message\n"
        logBuffer.append(logMessage)
    }

    private fun getLogFile(): File {
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

    private fun registerHandler() {
        Handler(Looper.getMainLooper()).post {
            while (!isCrashed) {
                try {
                    Looper.loop()
                } catch (e: Throwable) {
                    loggerCrashHandler(Looper.getMainLooper().thread, e)
                }
            }
        }

        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            if (!isCrashed)
                loggerCrashHandler(t, e)
        }
    }

    private fun cleanOldLogs() {
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

    private fun formatTime(): Long {
        return (DAYS_RETAINED * 24 * 60 * 60 + HOURS_RETAINED * 60 * 60 + MINUTES_RETAINED * 60 + SECONDS_RETAINED) * SECOND
    }
}

inline fun logV(tag: String = "", msg: String = "") =
    with(VERBOSE) {
        if (LOG_LEVEL <= this) {
            Log.v(tag, LOG_HEADER + getFunctionName() + msg)
            Logger.appendLog(getName(this), tag, LOG_HEADER + msg)
        }
    }

inline fun logD(tag: String = "", msg: String = "") =
    with(DEBUG) {
        if (LOG_LEVEL <= this) {
            Log.d(tag, LOG_HEADER + getFunctionName() + msg)
            Logger.appendLog(getName(this), tag, LOG_HEADER + msg)
        }
    }

inline fun logI(tag: String = "", msg: String = "") =
    with(INFO) {
        if (LOG_LEVEL <= this) {
            Log.i(tag, LOG_HEADER + getFunctionName() + msg)
            Logger.appendLog(getName(this), tag, LOG_HEADER + msg)
        }
    }

inline fun logW(tag: String = "", msg: String = "") =
    with(WARN) {
        if (LOG_LEVEL <= this) {
            Log.w(tag, LOG_HEADER + getFunctionName() + msg)
            Logger.appendLog(getName(this), tag, LOG_HEADER + msg)
        }
    }

inline fun logE(tag: String = "", msg: String = "") =
    with(ERROR) {
        if (LOG_LEVEL <= this) {
            Log.e(tag, LOG_HEADER + getFunctionName() + msg)
            Logger.appendLog(getName(this), tag, LOG_HEADER + msg)
        }
    }