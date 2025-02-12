package com.zephyr.app

import android.app.Application
import com.zephyr.base.log.LogLevel
import com.zephyr.base.log.Logger

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.startLogger(this, LogLevel.VERBOSE)
    }
}