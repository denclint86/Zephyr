package com.zephyr.scaling_layout

/**
 * Created by mertsimsek on 01/10/2017.
 *
 * Edited by NIKI on 2025/2/13
 */
interface ScalingLayoutListener {
    fun onCollapsed()

    fun onExpanded()

    fun onProgress(progress: Float)
}
