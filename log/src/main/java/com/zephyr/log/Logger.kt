package com.zephyr.log

import android.util.Log
import com.zephyr.log.interfaces.IBufferCtrl
import com.zephyr.log.interfaces.IExceptionHandler
import com.zephyr.log.interfaces.IFileManager
import com.zephyr.log.models.BufferCtrl
import com.zephyr.log.models.ExceptionHandler
import com.zephyr.log.models.FileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * 日志类, 对外暴露日志记录、异常捕捉监听设置
 */
class Logger {

    /**
     * 设置当捕捉到未被捕获的异常时的回调
     */
    fun setOnCaughtListener(l: ((thread: Thread, throwable: Throwable) -> Unit)?) {
        exceptionListener = l
    }

    fun log(level: LogLevel, tag: String = "", msg: String = "") {
        if (LogConfig.logLevel > level) return
        if (LogConfig.logLevel != LogLevel.DO_NOT_LOG)
            init()

        when (level) {
            LogLevel.VERBOSE -> Log.v(tag, msg)
            LogLevel.DEBUG -> Log.d(tag, msg)
            LogLevel.INFO -> Log.i(tag, msg)
            LogLevel.WARN -> Log.w(tag, msg)
            LogLevel.ERROR -> Log.e(tag, msg)
            LogLevel.DO_NOT_LOG -> return
        }

        if (LogConfig.writeToFile)
            addToBuffer(level, tag, msg)
    }


    private val buffer: IBufferCtrl by lazy {
        BufferCtrl()
    }

    private val fileManager: IFileManager by lazy {
        val fileIConfig = object : IFileManager.IConfig {
            override val filePath: String
                get() = LogConfig.fileFolder
            override val fileName: String
                get() = LogConfig.fileName
            override val cleanOODLogs: Boolean
                get() = LogConfig.cleanOODLogs
            override val logKeepDuration: Long
                get() = LogConfig.logKeepDuration
        }

        FileManager(fileIConfig)
    }

    private val exceptionHandler: IExceptionHandler by lazy {
        ExceptionHandler()
    }

    private val loggerScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var loggerJob: Job? = null

    private var exceptionListener: ((Thread, Throwable) -> Unit)? = null

    private var isInitialized = false
    private var lock = Any()

    private fun init() = synchronized(lock) {
        if (isInitialized) return
        launchWriteLogCoroutine()

        exceptionHandler.register()
        exceptionHandler.setOnCaughtListener { thread, throwable ->
            if (LogConfig.writeToFile) {
                val fullMsg = "EXCEPTION: ${thread.name}\n${throwable.stackTraceToString()}"
                addToBuffer(LogLevel.ERROR, "未捕捉的异常", fullMsg)
                fileManager.append(buffer.pop())
            }
            exceptionListener?.invoke(thread, throwable)
            (exceptionListener != null) // 如果注册了监听则认为已经处理异常, 返回真
        }
        isInitialized = true
    }

    private fun launchWriteLogCoroutine() {
        loggerJob?.cancel()
        loggerJob = loggerScope.launch {
            while (isActive) {
                var sleep = LogConfig.sleepTime
                if (LogConfig.writeToFile) {
                    fileManager.append(buffer.pop())
                    fileManager.cleanOld()
                } else {
                    sleep *= 5
                }
                delay(sleep)
            }
        }
    }

    private fun addToBuffer(level: LogLevel, tag: String, msg: String) {
        val logMessage = "${LogConfig.time} | ${level.text} | $tag\n$msg\n"
        buffer.add(logMessage)
    }
}
