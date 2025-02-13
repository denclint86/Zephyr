package com.zephyr.scaling_layout

import android.content.Context
import android.util.AttributeSet

/**
 * Created by mertsimsek on 30/09/2017.
 *
 * Edited by NIKI on 2025/2/13
 */
class ScalingLayoutSettings(context: Context, attributeSet: AttributeSet?) {
    private var radiusFactor: Float
    var initialWidth: Int = 0
        private set
    val maxWidth: Int
    var maxRadius: Float = 0f
        private set
    var elevation: Float = 0f
    var isInitialized: Boolean = false
        private set

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ScalingLayout)
        radiusFactor =
            typedArray.getFloat(R.styleable.ScalingLayout_radiusFactor, DEFAULT_RADIUS_FACTOR)
        maxWidth = context.resources.displayMetrics.widthPixels
        typedArray.recycle()

        if (radiusFactor > DEFAULT_RADIUS_FACTOR) {
            radiusFactor = DEFAULT_RADIUS_FACTOR
        }
    }

    fun initialize(width: Int, height: Int) {
        if (!isInitialized) {
            isInitialized = true
            initialWidth = width
            val radiusLimit = (height / 2).toFloat()
            maxRadius = radiusLimit * radiusFactor
        }
    }

    companion object {
        private const val DEFAULT_RADIUS_FACTOR = 1.0f
    }
}
