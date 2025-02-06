package com.zephyr.base

import android.app.Application
import android.content.Context

// 主要是一些拓展的函数

private var _appContext: Context? = null
val appContext: Context
    get() = _appContext ?: throw Exception("需要先在 application 中为 'appContext' 赋值")

fun Application.setAppContext(context: Context) {
    _appContext = context
}

var appBaseUrl = ""
var appIpAddress = ""
var appPreferenceName = "zephyr_datastore_pref"