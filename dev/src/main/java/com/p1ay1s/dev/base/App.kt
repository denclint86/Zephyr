package com.p1ay1s.dev.base

import android.app.Application
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class App : Application() {

    /**
     * 您需要为 Logger 设置:
     *
     * example:
     * Logger.crashActivity = YourCrashActivity::class.java
     * Logger.start(application, applicationContext, com.p1ay1s.dev.log.ERROR)
     */
    abstract fun whenOnCreate(appContext: Context)

    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch {
            while (applicationContext == null) {
                // 等到 context 可用为止
                delay(10)
            }
            withContext(Dispatchers.Main) {
                whenOnCreate(applicationContext)
            }
        }
    }
}