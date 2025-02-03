package com.zephyr.app

import android.app.Application
import com.zephyr.base.log.Logger
import com.zephyr.base.log.VERBOSE

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.startLogger(this, VERBOSE)
    }
}