package com.p1ay1s.extensions.view

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.p1ay1s.dev.base.ui.FragmentControllerView
import com.p1ay1s.dev.base.vb.ViewBindingFragment
import com.p1ay1s.extensions.databinding.FragmentContainerBinding

open class ContainerFragment(private val fragmentMap: LinkedHashMap<String, Fragment>) :
    ViewBindingFragment<FragmentContainerBinding>() {

    protected var controllerView: FragmentControllerView? = null

    override fun FragmentContainerBinding.initBinding() {
        fragmentControllerViewContainer.run {
            controllerView = this
            fragmentManager = childFragmentManager
            submitMap(fragmentMap)
            init()
        }
    }

    fun switchToFragment(index: String) =
        controllerView?.switchToFragment(index)

    fun <VB : ViewDataBinding> addFragment(
        index: String,
        fragment: ChildFragment<VB>,
        show: Boolean = true
    ) = controllerView?.addFragment(index, fragment, show)
}
