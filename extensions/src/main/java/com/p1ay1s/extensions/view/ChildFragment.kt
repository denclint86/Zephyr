package com.p1ay1s.extensions.view

import androidx.databinding.ViewDataBinding
import com.p1ay1s.dev.base.TAG
import com.p1ay1s.dev.base.log.logE
import com.p1ay1s.dev.base.vb.ViewBindingFragment

/**
 * 在父容器为 ContainerFragment 时有切换 fragment 的能力
 *
 * @see ContainerFragment
 */
abstract class ChildFragment<VB : ViewDataBinding> : ViewBindingFragment<VB>() {

    protected fun switchToFragment(index: String) = runCatching {
        (parentFragment as ContainerFragment).switchToFragment(index)
    }.onFailure {
        logE(TAG, "failed to call parent's switchToFragment")
    }

    protected fun addFragment(index: String, fragment: ChildFragment<VB>, show: Boolean = true) =
        runCatching {
            (parentFragment as ContainerFragment).addFragment(index, fragment, show)
        }.onFailure {
            logE(TAG, "failed to call parent's addAndSwitchToFragment")
        }
}