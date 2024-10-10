package com.p1ay1s.impl.ui

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * TODO 改用behavior
 * @param layoutId 布局id
 * @param collapsable 是否允许悬挂
 * @param collapseHeightPercent 悬挂时的高度百分比
 */
open class BaseBottomSheetDialogFragment(
    private val layoutId: Int,
    private val maxHeightPercent: Double = 0.9,
    private val collapsable: Boolean = false,
    private val collapseHeightPercent: Double = 0.5
) : BottomSheetDialogFragment() {
//
//    @SuppressLint("PrivateResource")
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        setStyle(
//            STYLE_NORMAL,
//            com.google.android.material.R.style.Theme_Design_Light_BottomSheetDialog
//        )
//        val dialog = BottomSheetDialog(requireContext(), theme).also {
//            it.setContentView(layoutId)
//
//            val radius = 70f // 圆角半径
//            val shape = GradientDrawable().apply {
//                shape = GradientDrawable.RECTANGLE
//                setColor(Color.WHITE) // 设置背景颜色
//                cornerRadii = floatArrayOf(
//                    radius, radius, // 左上角
//                    radius, radius, // 右上角
//                    0f, 0f,       // 右下角
//                    0f, 0f        // 左下角
//                )
//            }
//
//            it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.background =
//                shape
//        }
//        return dialog
//    }
//
//    override fun onStart() {
//        super.onStart()
//        setupBottomSheetBehavior()
//    }
//
//    private fun setupBottomSheetBehavior() {
//        dialog?.apply {
//            findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
//                ?.let { bottomSheet ->
//                    with(BottomSheetBehavior.from(bottomSheet)) {
//                        state = BottomSheetBehavior.STATE_EXPANDED
//                        skipCollapsed = !collapsable
//                        if (skipCollapsed) {
//                            val height =
//                                (resources.displayMetrics.heightPixels * collapseHeightPercent).toInt()
//                            peekHeight = height
//                        }
//                    }
//                    bottomSheet.layoutParams = bottomSheet.layoutParams.apply {
//                        height = (resources.displayMetrics.heightPixels * maxHeightPercent).toInt()
//                    }
//                }
//        }
//    }
}