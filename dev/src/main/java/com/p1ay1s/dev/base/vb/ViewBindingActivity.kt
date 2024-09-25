package com.p1ay1s.dev.base.vb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding

abstract class ViewBindingActivity<VB : ViewDataBinding> : AppCompatActivity(),
    ViewBindingInterface<VB> {

    /**
     * 函数内可直接引用控件id
     */
    abstract fun VB.initBinding()

    // protected val mBinding: VB by lazy(mode = LazyThreadSafetyMode.NONE) {
    protected val mBinding: VB by lazy {
        getViewBinding(layoutInflater)
    }

    /**
     * 子类的 super 方法包含了 initBinding, 可以据此安排代码
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.initBinding()
    }

    /**
     * 防止内存泄露
     */
    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}