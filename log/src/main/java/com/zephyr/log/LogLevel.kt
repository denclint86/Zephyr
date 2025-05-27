package com.zephyr.log

enum class LogLevel(val text: String) {
    VERBOSE("详细"),
    DEBUG("调试"),
    INFO("信息"),
    WARN("警告"),
    ERROR("错误"),
    DO_NOT_LOG("不记录日志")
}