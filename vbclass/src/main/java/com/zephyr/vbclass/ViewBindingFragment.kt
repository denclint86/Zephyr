package com.zephyr.vbclass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 * @see ViewBindingInterface 注意事项
 */
abstract class ViewBindingFragment<VB : ViewDataBinding> : Fragment(),
    ViewBindingInterface<VB> {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!

    /**
     * 函数内可直接引用控件id
     */
    abstract fun VB.onViewCreated()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.onViewCreated()
    }

    /**
     * 防止内存泄漏
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding?.unbind()
        _binding = null
    }
}