package com.zephyr.base

import android.content.Context
import android.util.Log

// 主要是一些拓展的函数

var appContext: Context? = null
    get() {
        if (field == null) {
            Log.e(
                "Zephyr",
                "你需要在 application 中为 'appContext' 赋值, 本依赖的部分方法十分依赖这个实例!"
            )
        }
        return field
    }

var appBaseUrl = ""
var appIpAddress = ""
var appPreferenceName = "zephyr_datastore_pref"