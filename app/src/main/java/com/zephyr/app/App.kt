package com.zephyr.app

import android.app.Application
import com.zephyr.base.appContext
import com.zephyr.base.log.Logger
import com.zephyr.base.log.VERBOSE

class App :Application(){
    override fun onCreate() {
        super.onCreate()
        appContext=this
        Logger.setLogLevel(VERBOSE)
    }
}