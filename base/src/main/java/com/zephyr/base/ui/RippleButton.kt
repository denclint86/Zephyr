package com.zephyr.base.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

open class RippleButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr) {

    var rippleColor = "#40FFFFFF"

    private val imgList: LinkedHashMap<Int, Int> = linkedMapOf()

    init {
        scaleType = ScaleType.FIT_CENTER

        background = RippleDrawable(
            ColorStateList.valueOf(Color.parseColor(rippleColor)), null, ShapeDrawable(OvalShape())
        )
    }

    fun setImageResources(map: LinkedHashMap<Int, Int>) {
        imgList.putAll(map)
        switchImage(map.keys.first())
    }

    fun switchImage(target: Int) {
        imgList[target]?.let { setImageResource(it) }
    }

    override fun onDraw(canvas: Canvas) {
        cutView()
        super.onDraw(canvas)
    }

    /**
     * 设置等宽高
     */
    protected fun cutView() {
        layoutParams.run {
            width = minOf(width, height)
            height = width
            requestLayout()
        }
    }
}