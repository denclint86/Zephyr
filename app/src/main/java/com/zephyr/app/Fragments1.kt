package com.zephyr.app

import com.zephyr.app.databinding.FragmentDefaultBinding
import com.zephyr.base.ui.findHost
import com.zephyr.vbclass.ViewBindingFragment

class Fragment1a : ViewBindingFragment<FragmentDefaultBinding>() {
    override fun FragmentDefaultBinding.initBinding() {
        root.setBackgroundColor(resources.getColor(R.color._1a))
        root.setOnClickListener {
            findHost()?.pushFragment(
                _1b,
                Fragment1b::class.java,
                R.anim.right_enter,
                R.anim.fade_out
            )
        }
    }
}

class Fragment1b : ViewBindingFragment<FragmentDefaultBinding>() {
    override fun FragmentDefaultBinding.initBinding() {
        root.setBackgroundColor(resources.getColor(R.color._1b))
        root.setOnClickListener {
            findHost()?.pushFragment(
                _1c,
                Fragment1c::class.java,
                R.anim.right_enter,
                R.anim.fade_out
            )
        }
    }
}

class Fragment1c : ViewBindingFragment<FragmentDefaultBinding>() {
    override fun FragmentDefaultBinding.initBinding() {
        root.setBackgroundColor(resources.getColor(R.color._1c))
        root.setOnClickListener {
            findHost()?.navigateFragment(
                _1a,
                R.anim.fade_in,
                R.anim.right_exit
            )
        }
    }
}
