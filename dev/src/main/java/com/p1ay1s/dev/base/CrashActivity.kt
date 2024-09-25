package com.p1ay1s.dev.base

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.p1ay1s.dev.databinding.ActivityCrashBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 必须开启 databinding
 *
 * 需要在项目中注册您继承的子类并给 Logger.crashActivity 赋值 YourChildActivity::java.class
 */
open class CrashActivity : AppCompatActivity() {
    private var backPressTimer: Job? = null
    private var oneMoreToFinish = false

    private val binding = ActivityCrashBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.run {
            setContentView(root)
            title.text = intent.getStringExtra("TITLE") ?: "empty"
            detail.text = intent.getStringExtra("DETAIL") ?: "empty"
            finish.setOnClickListener {
                finishAffinity()
            }
        }
    }

    /**
     * 简单的退出逻辑
     */
    protected fun handleBackPressed() {
        if (oneMoreToFinish) {
            finishAffinity()
        } else {
            oneMoreToFinish = true

            toast("再次点击退出")
            backPressTimer?.cancel()
            backPressTimer = lifecycleScope.launch {
                delay(2000)
                oneMoreToFinish = false
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @CallSuper
    override fun onBackPressed() {
        handleBackPressed()
    }
}