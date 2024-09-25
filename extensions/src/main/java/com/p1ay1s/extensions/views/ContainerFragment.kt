package com.p1ay1s.extensions.views

import androidx.fragment.app.Fragment
import com.p1ay1s.dev.base.ui.FragmentControllerView
import com.p1ay1s.dev.base.vb.ViewBindingFragment
import com.p1ay1s.extensions.databinding.FragmentContainerBinding

open class ContainerFragment(private val fragmentMap: LinkedHashMap<String, Fragment>) :
    ViewBindingFragment<FragmentContainerBinding>() {

    var controllerView: FragmentControllerView? = null

    override fun FragmentContainerBinding.initBinding() {
        fragmentControllerViewContainer.run {
            controllerView = this
            fragmentManager = childFragmentManager
            submitMap(fragmentMap)
            init()
        }
    }
}
