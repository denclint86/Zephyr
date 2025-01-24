package com.zephyr.app

import com.zephyr.app.databinding.FragmentDefaultBinding
import com.zephyr.base.ui.findHost
import com.zephyr.vbclass.ViewBindingFragment

class Fragment3a : ViewBindingFragment<FragmentDefaultBinding>() {
    override fun FragmentDefaultBinding.initBinding() {
        root.setBackgroundColor(resources.getColor(R.color._3a))
        root.setOnClickListener {
            findHost()?.pushFragment(
                _3b,
                Fragment3b::class.java,
                R.anim.right_enter,
                R.anim.fade_out
            )
        }
    }
}

class Fragment3b : ViewBindingFragment<FragmentDefaultBinding>() {
    override fun FragmentDefaultBinding.initBinding() {
        root.setBackgroundColor(resources.getColor(R.color._3b))
        root.setOnClickListener {
            findHost()?.pushFragment(
                _3c,
                Fragment3c::class.java,
                R.anim.right_enter,
                R.anim.fade_out
            )
        }
    }
}

class Fragment3c : ViewBindingFragment<FragmentDefaultBinding>() {
    override fun FragmentDefaultBinding.initBinding() {
        root.setBackgroundColor(resources.getColor(R.color._3c))
        root.setOnClickListener {
            findHost()?.navigateFragment(
                _3a,
                R.anim.fade_in,
                R.anim.right_exit
            )
        }
    }
}