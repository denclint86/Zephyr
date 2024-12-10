package com.p1ay1s.base

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.CallSuper
import com.p1ay1s.base.databinding.ActivityCrashBinding
import com.p1ay1s.base.extension.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 用于展示崩溃情况的 activity
 * 必须开启 databinding
 *
 * 直接用您的 activity ,或在项目中注册您继承的子类并给 Logger.crashActivity 赋值 YourChildActivity::java.class
 */
@Deprecated("")
abstract class CrashActivity : Activity(), ActivityPreferences.TwoClicksListener {
    protected var backPressTimer: Job? = null
    protected var oneMoreToFinish = false

    protected lateinit var binding: ActivityCrashBinding
    protected lateinit var crashScope: CoroutineScope

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
        twoClicksToExit()
    }

    override fun twoClicksToExit() {
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