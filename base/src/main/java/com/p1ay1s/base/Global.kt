package com.p1ay1s.base

import android.content.Context
import com.p1ay1s.base.log.logE

// 主要是一些拓展的函数

var appContext: Context? = null
    get() {
        if (field == null) {
            logE("Zephyr", "you have to init appContext first!")
        }
        return field
    }
var appBaseUrl = ""
var appIpAddress = ""