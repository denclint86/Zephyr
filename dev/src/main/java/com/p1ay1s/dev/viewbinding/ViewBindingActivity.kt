package com.p1ay1s.dev.viewbinding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.p1ay1s.dev.base.TAG
import com.p1ay1s.dev.log.logI

abstract class ViewBindingActivity<VB : ViewDataBinding> : AppCompatActivity(),
    ViewBindingInterface<VB> {

    /**
     * 函数内可直接引用控件id
     */
    abstract fun VB.initBinding()

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!

    /**
     * 子类的 super 方法包含了 initBinding, 可以据此安排代码
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        logI(TAG, "$TAG onCreate", false)
        super.onCreate(savedInstanceState)
        _binding = getViewBinding(layoutInflater)
        setContentView(binding.root)
        binding.initBinding()
    }

    override fun onStart() {
        logI(TAG, "$TAG onStart", false)
        super.onStart()
    }

    override fun onResume() {
        logI(TAG, "$TAG onResume", false)
        super.onResume()
    }

    override fun onPause() {
        logI(TAG, "$TAG onPause", false)
        super.onPause()
    }

    override fun onStop() {
        logI(TAG, "$TAG onStop", false)
        super.onStop()
    }

    /**
     * 防止内存泄露
     */
    override fun onDestroy() {
        logI(TAG, "$TAG onDestroy", false)
        super.onDestroy()
        _binding?.unbind()
        _binding = null
    }
}