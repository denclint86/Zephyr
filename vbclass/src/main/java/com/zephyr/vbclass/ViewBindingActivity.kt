package com.zephyr.vbclass

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding

/**
 * @see ViewBindingInterface 注意事项
 */
abstract class ViewBindingActivity<VB : ViewDataBinding> : AppCompatActivity(),
    ViewBindingInterface<VB> {

    /**
     * 函数内可直接引用控件id
     */
    abstract fun VB.onCreate()

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
        binding.onCreate()
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