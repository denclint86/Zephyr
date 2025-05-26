package com.zephyr.tools

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import com.zephyr.provider.Zephyr

class WinParamsHelper private constructor(private val context: Context) {
    companion object {
        fun attachTo(context: Context) = WinParamsHelper(context)
    }

    constructor() : this(Zephyr.application)

    val statusBarHeight: Int
        get() = context.getStatusBarHeight()

    val navigationBarHeight: Int
        get() = context.getNavigationBarHeight()

    val winWidth: Int
        get() = context.getWinWidth()

    val winHeight: Int
        get() = context.getWinHeight()

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    private fun Context.getStatusBarHeight(): Int {
        var h = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            h = resources.getDimensionPixelSize(resourceId)
        }
        return h
    }

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    private fun Context.getNavigationBarHeight(): Int {
        var h = 0
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            h = resources.getDimensionPixelSize(resourceId)
        }
        return h
    }

    private fun Context.getWinWidth(): Int {
        val w = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = getSystemService(WindowManager::class.java).maximumWindowMetrics
            windowMetrics.bounds.width()
        } else {
            val displayMetrics = DisplayMetrics()
            getSystemService(WindowManager::class.java).defaultDisplay.getRealMetrics(
                displayMetrics
            )
            displayMetrics.widthPixels
        }
        return w
    }

    private fun Context.getWinHeight(): Int {
        val h = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = getSystemService(WindowManager::class.java).maximumWindowMetrics
            windowMetrics.bounds.height()
        } else {
            val displayMetrics = DisplayMetrics()
            getSystemService(WindowManager::class.java).defaultDisplay.getRealMetrics(
                displayMetrics
            )
            displayMetrics.heightPixels
        }
        return h
    }
}