package com.zephyr.log.models

import com.zephyr.log.appendString
import com.zephyr.log.deleteChildIf
import com.zephyr.log.interfaces.IFileManager
import com.zephyr.provider.Zephyr
import java.io.File

/**
 * 日志文件写入控制
 */
internal class FileManager(
    private val config: IFileManager.IConfig
) : IFileManager {
    private val fileDir: File
        get() {
            val dir = File(Zephyr.application.getExternalFilesDir(null), config.filePath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return dir
        }

    private val file: File
        get() = File(fileDir, config.fileName)

    override fun append(string: String) {
        file.appendString(string)
    }

    override fun cleanOld() {
        if (!config.cleanOODLogs) return

        val outOfDateTimestamp =
            System.currentTimeMillis() - config.logKeepDuration

        fileDir.deleteChildIf {
            lastModified() < outOfDateTimestamp
        }
    }
}