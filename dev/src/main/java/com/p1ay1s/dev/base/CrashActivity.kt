package com.p1ay1s.dev.base

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.CallSuper
import com.p1ay1s.dev.databinding.ActivityCrashBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 必须开启 databinding
 *
 * 直接用您的 activity ,或在项目中注册您继承的子类并给 Logger.crashActivity 赋值 YourChildActivity::java.class
 */
open class CrashActivity : Activity(), ActivityPreferences.TwoBackPressToExit {
    private var backPressTimer: Job? = null
    private var oneMoreToFinish = false

    private lateinit var binding: ActivityCrashBinding
    private lateinit var crashScope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCrashBinding.inflate(LayoutInflater.from(this))
        crashScope = CoroutineScope(Dispatchers.IO)

        binding.run {
            setContentView(root)
            title.text = intent.getStringExtra("TITLE") ?: "empty"
            detail.text = intent.getStringExtra("DETAIL") ?: "empty"
            finishButton.setOnClickListener {
                finishAffinity()
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @CallSuper
    override fun onBackPressed() {
        twoBackPressToExit()
    }

    override fun twoBackPressToExit() {
        if (oneMoreToFinish) {
            finishAffinity()
        } else {
            oneMoreToFinish = true

            toast("再次点击退出")
            backPressTimer?.cancel()
            backPressTimer = crashScope.launch {
                delay(2000)
                oneMoreToFinish = false
            }
        }
    }
}