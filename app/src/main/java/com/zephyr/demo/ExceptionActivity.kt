package com.zephyr.demo

import com.zephyr.demo.databinding.ActivityExceptionBinding
import com.zephyr.vbclass.ViewBindingActivity

class ExceptionActivity : ViewBindingActivity<ActivityExceptionBinding>() {
    override fun ActivityExceptionBinding.onCreate() {
        val tn = intent.getStringExtra("tn")
        val st = intent.getStringExtra("st")

        tv.text = "EXCEPTION: $tn\n$st"
    }
}