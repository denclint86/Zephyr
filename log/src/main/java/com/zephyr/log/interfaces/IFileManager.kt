package com.zephyr.log.interfaces

internal interface IFileManager {
    fun append(string: String)
    fun cleanOld()

    interface IConfig {
        val filePath: String
        val fileName: String

        val cleanOODLogs: Boolean

        val logKeepDuration: Long
    }
}