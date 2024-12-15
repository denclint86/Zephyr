package com.zephyr.base

import android.app.Application
import android.content.Context

@Deprecated("")
abstract class App : Application() {

    /**
     * 您需要为设置以下代码来启用 Logger:
     *
     * Logger.crashActivity = YourCrashActivity::class.java
     * startLogger(context, com.zephyr.dev.log.ERROR)
     */
    @Deprecated("")
    fun whenOnCreate(context: Context) {
    }
}