package com.p1ay1s.base

import android.app.Application
import android.content.Context

abstract class App : Application() {

    /**
     * 您需要为设置以下代码来启用 Logger:
     *
     * Logger.crashActivity = YourCrashActivity::class.java
     * startLogger(context, com.p1ay1s.dev.log.ERROR)
     */
    abstract fun whenOnCreate(context: Context)

    override fun onCreate() {
        super.onCreate()
        whenOnCreate(this)
    }
}