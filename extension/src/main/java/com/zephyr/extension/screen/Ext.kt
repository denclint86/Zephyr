package com.zephyr.extension.screen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import com.zephyr.log.logE

private const val tag = "screen extension"

@SuppressLint("InternalInsetResource", "DiscouragedApi")
fun Context.getStatusBarHeight(): Int {
    var h = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        h = resources.getDimensionPixelSize(resourceId)
    }
    logE(tag, "status bar height: $h")
    return h
}

@SuppressLint("InternalInsetResource", "DiscouragedApi")
fun Context.getNavigationBarHeight(): Int {
    var h = 0
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
    if (resourceId > 0) {
        h = resources.getDimensionPixelSize(resourceId)
    }
    logE(tag, "navigation var height: $h")
    return h
}

fun Context.getWinWidth(): Int {
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
    logE(tag, "win width: $w")
    return w
}

fun Context.getWinHeight(): Int {
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
    logE(tag, "win height: $h")
    return h
}

/**
 * 获得的应为整个手机屏幕的高度
 */
fun Context.getScreenHeight(): Int {
    val s = getStatusBarHeight()
    val w = getWinHeight()
    val n = getNavigationBarHeight()
    val h = s + n + w
    logE(tag, "screen height: $h")
    return h
}