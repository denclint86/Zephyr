package com.zephyr.app

import com.zephyr.app.databinding.FragmentDefaultBinding
import com.zephyr.extension.ui.fragmenthost.findHost
import com.zephyr.vbclass.ViewBindingFragment

class Fragment2a : ViewBindingFragment<FragmentDefaultBinding>() {
    override fun FragmentDefaultBinding.initBinding() {
        root.setBackgroundColor(resources.getColor(R.color._2a))
        root.setOnClickListener {
            findHost()?.pushFragment(
                _2b,
                Fragment2b::class.java,
                R.anim.right_enter,
                R.anim.fade_out
            )
        }
    }
}

class Fragment2b : ViewBindingFragment<FragmentDefaultBinding>() {
    override fun FragmentDefaultBinding.initBinding() {
        root.setBackgroundColor(resources.getColor(R.color._2b))
        root.setOnClickListener {
            findHost()?.pushFragment(
                _2c,
                Fragment2c::class.java,
                R.anim.right_enter,
                R.anim.fade_out
            )
        }
    }
}

class Fragment2c : ViewBindingFragment<FragmentDefaultBinding>() {
    override fun FragmentDefaultBinding.initBinding() {
        root.setBackgroundColor(resources.getColor(R.color._2c))
        root.setOnClickListener {
            findHost()?.navigateFragment(
                _2a,
                R.anim.fade_in,
                R.anim.right_exit
            )
        }
    }
}