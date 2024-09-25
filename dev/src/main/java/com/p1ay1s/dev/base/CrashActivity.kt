package com.p1ay1s.dev.base

import androidx.lifecycle.lifecycleScope
import com.p1ay1s.dev.base.vb.ViewBindingActivity
import com.p1ay1s.dev.databinding.ActivityCrashBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class CrashActivity : ViewBindingActivity<ActivityCrashBinding>() {
    private var backPressTimer: Job? = null
    private var oneMoreToFinish = false

    override fun ActivityCrashBinding.initBinding() {
        title.text = intent.getStringExtra("TITLE") ?: "empty"
        detail.text = intent.getStringExtra("DETAIL") ?: "empty"
    }

    /**
     * 简单的退出逻辑
     */
    private fun handleBackPressed() {
        if (oneMoreToFinish) {
            finishAfterTransition()
        } else {
            oneMoreToFinish = true
            backPressTimer?.cancel()
            backPressTimer = lifecycleScope.launch {
                delay(2000)
                oneMoreToFinish = false
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        handleBackPressed()
        super.onBackPressed()
    }
}