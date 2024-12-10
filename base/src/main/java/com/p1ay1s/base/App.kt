package com.p1ay1s.base

import android.app.Application
import android.content.Context

@Deprecated("")
abstract class App : Application() {

    /**
     * 您需要为设置以下代码来启用 Logger:
     *
     * Logger.crashActivity = YourCrashActivity::class.java
     * startLogger(context, com.p1ay1s.dev.log.ERROR)
     */
    @Deprecated("")
    fun whenOnCreate(context: Context) {
    }
}