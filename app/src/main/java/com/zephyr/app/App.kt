package com.zephyr.app

import android.app.Application
import com.zephyr.log.LogLevel
import com.zephyr.log.Logger

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.startLogger(this, LogLevel.VERBOSE)
    }
}