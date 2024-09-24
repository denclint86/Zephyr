package com.p1ay1s.dev.base.vb

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class ViewBindingFragment<VB : ViewDataBinding> : Fragment(),
    ViewBindingInterface<VB> {

    protected lateinit var mBinding: VB

    /**
     * 函数内可直接引用控件id
     *
     * example:
     * @see ContainerFragment
     */
    abstract fun VB.initBinding()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = getViewBinding(inflater, container)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.initBinding()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放, 防止内存泄漏
        if (::mBinding.isInitialized) {
            mBinding.unbind()
        }
    }
}