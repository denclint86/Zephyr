package com.zephyr.provider

import android.app.Application

val Any.TAG: String
    get() = this::class.java.simpleName

object Zephyr {
    lateinit var application: Application

    val packageName: String
        get() = application.packageName

    var preferenceName = "zephyr"
}