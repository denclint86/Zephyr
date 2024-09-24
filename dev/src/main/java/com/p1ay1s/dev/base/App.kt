package com.p1ay1s.dev.base

import android.app.Application
import android.content.Context
import com.p1ay1s.dev.base.log.Logger

lateinit var appContext: Context

open class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
        Logger.start()
    }
}