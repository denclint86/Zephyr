package com.p1ay1s.extensions.views

import androidx.databinding.ViewDataBinding
import com.p1ay1s.dev.ui.FragmentControllerView
import com.p1ay1s.dev.viewbinding.ViewBindingFragment

/**
 * 在父容器为 ContainerFragment 时有切换 fragment 的能力
 *
 * @see ContainerFragment
 */
abstract class ChildFragment<VB : ViewDataBinding> : ViewBindingFragment<VB>(),
    FragmentControllerView.OnPassDataListener {

    protected fun getControllerView(): FragmentControllerView? =
        parentFragment.run {
            if (this is ContainerFragment)
                return this.controllerView
            return null
        }
}