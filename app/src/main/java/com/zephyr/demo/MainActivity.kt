package com.zephyr.demo

import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zephyr.demo.databinding.ActivityMainBinding
import com.zephyr.log.LogConfig
import com.zephyr.log.LogLevel
import com.zephyr.log.logD
import com.zephyr.log.logE
import com.zephyr.log.logI
import com.zephyr.log.logV
import com.zephyr.log.logW
import com.zephyr.log.setOnCaughtListener
import com.zephyr.vbclass.ViewBindingActivity

class MainActivity : ViewBindingActivity<ActivityMainBinding>() {

    override fun ActivityMainBinding.onCreate() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        LogConfig.edit {
            logFileHeader = "日志"
            writeToFile = true
            logLevel = LogLevel.DEBUG
        }

        setOnCaughtListener { thread, throwable ->
            val i = Intent(this@MainActivity, ExceptionActivity::class.java).apply {
                putExtra("tn", thread.name)
                putExtra("st", throwable.stackTraceToString())
            }
            startActivity(i)
        }

        logV("TAG", "this is a log message!!!")
        logD("TAG", "this is a log message!!!")
        logI("TAG", "this is a log message!!!")
        logW("TAG", "this is a log message!!!")
        logE("TAG", "this is a log message!!!")

        main.setOnClickListener {
//            GlobalScope.launch {
            throw Exception("test")
//            }
        }
    }
}