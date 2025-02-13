package com.zephyr.global_values

import android.app.Application

val Any.TAG: String
    get() = this::class.java.simpleName

var globalContext: Application? = null
    get() {
        if (field == null) throw Exception("global bal context not initialized")
        return field
    }

var globalPreferenceName = "zephyr"