package com.p1ay1s.vbclass.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.p1ay1s.vbclass.ViewBindingInterface

abstract class ViewBindingDialogFragment<VB : ViewDataBinding>(
    private val expandedHeightPercent: Double = MAX_HEIGHT_PERCENT
) : BottomSheetDialogFragment(), ViewBindingInterface<VB> {

    companion object {
        const val MAX_HEIGHT_PERCENT = 0.75
    }

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!

    abstract fun VB.initBinding()

    @SuppressLint("PrivateResource")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(
            STYLE_NORMAL,
            com.google.android.material.R.style.Theme_Design_Light_BottomSheetDialog
        )

        return BottomSheetDialog(requireContext(), theme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(layoutInflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.initBinding()
    }

    override fun onStart() {
        super.onStart()
        setupBottomSheetBehavior()
    }

    private fun setupBottomSheetBehavior() {
        dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.let { bottomSheet ->
                with(BottomSheetBehavior.from(bottomSheet)) {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    skipCollapsed = true
                }
                bottomSheet.layoutParams = bottomSheet.layoutParams.apply {
                    height =
                        (resources.displayMetrics.heightPixels * expandedHeightPercent).toInt()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding?.unbind()
        _binding = null
    }
}