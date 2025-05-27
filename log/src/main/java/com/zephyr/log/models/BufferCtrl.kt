package com.zephyr.log.models

import com.zephyr.log.interfaces.IBufferCtrl

/**
 * 日志文件缓冲控制
 */
internal class BufferCtrl : IBufferCtrl {
    private val sb = StringBuffer()

    override fun pop(): String =
        sb.toString().also {
            sb.setLength(0)
        }

    override fun add(string: String) {
        sb.append(string)
    }
}