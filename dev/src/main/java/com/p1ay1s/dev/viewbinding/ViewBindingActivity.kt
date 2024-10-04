package com.p1ay1s.dev.viewbinding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding

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
        super.onCreate(savedInstanceState)
        _binding = getViewBinding(layoutInflater)
        setContentView(binding.root)
        binding.initBinding()
    }

    /**
     * 防止内存泄露
     */
    override fun onDestroy() {
        super.onDestroy()
        _binding?.unbind()
        _binding = null
    }
}