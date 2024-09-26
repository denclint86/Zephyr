package com.p1ay1s.dev.base

import android.app.Application
import android.content.Context

abstract class App : Application() {

    /**
     * 您需要为 Logger 设置:
     *
     * example:
     * Logger.crashActivity = YourCrashActivity::class.java
     * Logger.start(application, baseContext, com.p1ay1s.dev.log.ERROR)
     */
    abstract fun whenAttachBaseContext(base: Context)

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        whenAttachBaseContext(base)
    }
}