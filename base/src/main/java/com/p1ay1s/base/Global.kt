package com.p1ay1s.base

import android.content.Context
import android.util.Log
import com.p1ay1s.base.log.logE

// 主要是一些拓展的函数

var appContext: Context? = null
    get() {
        if (field == null) {
            Log.e("Zephyr", "need to init appContext(at Global.kt) first!")
        }
        return field
    }
var appBaseUrl = ""
var appIpAddress = ""