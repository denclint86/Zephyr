package com.p1ay1s.dev.viewbinding

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.p1ay1s.dev.base.TAG
import com.p1ay1s.dev.log.logI

abstract class ViewBindingFragment<VB : ViewDataBinding> : Fragment(),
    ViewBindingInterface<VB> {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!

    /**
     * 函数内可直接引用控件id
     */
    abstract fun VB.initBinding()

    override fun onAttach(context: Context) {
        logI(TAG, "$TAG on", false)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        logI(TAG, "$TAG on", false)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logI(TAG, "$TAG onCreateView", false)
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logI(TAG, "$TAG onViewCreated", false)
        super.onViewCreated(view, savedInstanceState)
        binding.initBinding()
    }

    override fun onStart() {
        logI(TAG, "$TAG onStart", false)
        super.onStart()
    }

    override fun onResume() {
        logI(TAG, "$TAG onResume", false)
        super.onResume()
    }

    override fun onPause() {
        logI(TAG, "$TAG onPause", false)
        super.onPause()
    }

    override fun onStop() {
        logI(TAG, "$TAG onStop", false)
        super.onStop()
    }

    override fun onDestroyView() {
        logI(TAG, "$TAG onDestroyView", false)
        super.onDestroyView()
        _binding?.unbind()
        _binding = null
    }

    override fun onDestroy() {
        logI(TAG, "$TAG onDestroy", false)
        super.onDestroy()
    }
}