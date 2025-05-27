package com.zephyr.log

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * 日志模块的配置, 可以动态更新
 */
object LogConfig {
    // --------------------- 日志级别与输出控制 ---------------------
    var logLevel: LogLevel = LogLevel.DO_NOT_LOG
        private set

    var writeToFile: Boolean = false
        private set

    var sleepTime: Long = 10_000L
        private set

    // --------------------- 文件存储配置 ---------------------
    var fileFolder: String = ""
        private set

    var logFileHeader: String = "日志"
        private set

    var logFileType: String = ".txt"
        private set

    val fileName: String
        get() = "$logFileHeader$date$logFileType"

    // --------------------- 时间格式与清理策略 ---------------------
    var dateFormat: String = "yyyy年MM月dd日"
        private set

    var timeFormat: String = "HH:mm:ss"
        private set

    var cleanOODLogs: Boolean = true
        private set

    var retainedDays: Int = 7
        private set

    var retainedHours: Int = 0
        private set

    var retainedMinutes: Int = 0
        private set

    var retainedSeconds: Int = 0
        private set

    val date: String
        get() = SimpleDateFormat(dateFormat, Locale.getDefault()).format(Date())

    val time: String
        get() = SimpleDateFormat(timeFormat, Locale.getDefault()).format(Date())

    val logKeepDuration: Long
        get() {
            val duration = retainedDays.days +
                    retainedHours.hours +
                    retainedMinutes.minutes +
                    retainedSeconds.seconds
            return duration.inWholeMilliseconds
        }

    // --------------------- DSL 配置入口 ---------------------
    fun edit(block: LogConfigBuilder.() -> Unit) {
        val builder = LogConfigBuilder()
        builder.block()
        builder.applyTo(this)
    }

    // DSL Builder 类
    class LogConfigBuilder {
        // 日志级别与输出控制
        var logLevel: LogLevel? = null
        var writeToFile: Boolean? = null
        var sleepTime: Long? = null

        // 文件存储配置
        var fileFolder: String? = null
        var logFileHeader: String? = null
        var logFileType: String? = null

        // 时间格式与清理策略
        var dateFormat: String? = null
        var timeFormat: String? = null
        var cleanOODLogs: Boolean? = null
        var retainedDays: Int? = null
        var retainedHours: Int? = null
        var retainedMinutes: Int? = null
        var retainedSeconds: Int? = null

        internal fun applyTo(config: LogConfig) {
            logLevel?.let { config.logLevel = it }
            writeToFile?.let { config.writeToFile = it }
            sleepTime?.let { config.sleepTime = it }
            fileFolder?.let { config.fileFolder = it }
            logFileHeader?.let { config.logFileHeader = it }
            logFileType?.let { config.logFileType = it }
            dateFormat?.let { config.dateFormat = it }
            timeFormat?.let { config.timeFormat = it }
            cleanOODLogs?.let { config.cleanOODLogs = it }
            retainedDays?.let { config.retainedDays = it }
            retainedHours?.let { config.retainedHours = it }
            retainedMinutes?.let { config.retainedMinutes = it }
            retainedSeconds?.let { config.retainedSeconds = it }
        }
    }
}