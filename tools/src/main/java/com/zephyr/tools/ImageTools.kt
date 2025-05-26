package com.zephyr.tools

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.core.graphics.drawable.toBitmap

fun Drawable?.copy(): Drawable? = this?.constantState?.newDrawable()?.mutate()

fun getPlaceholderBitmap(radius: Int, color: String? = "#90909080"): Bitmap {
    return getPlaceholderDrawable(radius, color, 1).toBitmap()
}

fun getPlaceholderDrawable(radius: Int, color: String? = "#90909080", size: Int? = null) =
    GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        if (radius != 0)
            cornerRadius = radius * 1.2F // 设置圆角半径
        if (size != null)
            setSize(size, size)
        setColor(Color.parseColor(color)) // 设置颜色
    }