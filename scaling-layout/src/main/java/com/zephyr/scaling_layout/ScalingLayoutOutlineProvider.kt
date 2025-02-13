package com.zephyr.scaling_layout

import android.graphics.Outline
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider

/**
 * Created by mertsimsek on 08/01/2018.
 *
 * Edited by NIKI on 2025/2/13
 */
class ScalingLayoutOutlineProvider(
    var width: Int,
    var height: Int,
    var radius: Float
) : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        outline.setRoundRect(0, 0, width, height, radius)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) view.clipToOutline =
            true // 关键修改: 解决安卓 9 以上圆角失效的问题
    }
}
