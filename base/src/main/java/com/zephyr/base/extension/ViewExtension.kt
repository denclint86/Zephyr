@file:Suppress("FunctionName")

package com.zephyr.base.extension

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.zephyr.base.appContext
import com.zephyr.base.ui.FragmentHost
import com.zephyr.base.ui.FragmentHostView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val RADIUS = 25

val Activity.TAG
    get() = this::class.simpleName!!
val Fragment.TAG
    get() = this::class.simpleName!!

fun View.setSize(size: Int) = setSize(size, size)

fun View.setSize(width: Int? = null, height: Int? = null) = runCatching {
    val lp = layoutParams
    lp?.run {
        width?.let { this.width = it }
        height?.let { this.height = it }
    } ?: return@runCatching
    layoutParams = lp
    requestLayout()
}

fun View.setHorizontalMargins(margin: Int) {
    setMargins(start = margin, end = margin)
}

fun View.setVerticalMargins(margin: Int) {
    setMargins(top = margin, bottom = margin)
}

fun View.setMargins(margin: Int) {
    setMargins(margin, margin, margin, margin)
}

fun View.setMargins(
    start: Int? = null,
    end: Int? = null,
    top: Int? = null,
    bottom: Int? = null,
) = runCatching {
    val lp = layoutParams
    (lp as? ViewGroup.MarginLayoutParams)?.run {
        start?.let { leftMargin = it }
        end?.let { rightMargin = it }
        top?.let { topMargin = it }
        bottom?.let { bottomMargin = it }
    } ?: return@runCatching
    layoutParams = lp
    requestLayout()
}

fun Activity.restartApplication() {
    packageManager.getLaunchIntentForPackage(packageName)?.run {
        this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(this)
        Runtime.getRuntime().exit(0)
    }
}

@Deprecated("")
fun Fragment.findHost(): FragmentHost? {
    var view = view
    var parent = view?.parent

    while (parent != null) {
        if (parent is FragmentHostView) {
            return parent.getActiveHost()
        }
        view = parent as? View // as? 如果转换失败则变为 null
        parent = view?.parent
    }
    return null
}

@SuppressLint("CheckResult")
fun ImageView.loadRadiusImage(
    imgUrl: String,
    radius: Int = RADIUS,
    enableCrossFade: Boolean = true,
) = set(imgUrl) {
    if (enableCrossFade)
        transition(DrawableTransitionOptions.withCrossFade())
    transform(CenterCrop(), RoundedCorners(radius))
}

@SuppressLint("CheckResult")
fun ImageView.loadCircleImage(
    imgUrl: String,
    enableCrossFade: Boolean = false,
) = set(imgUrl) {
    if (enableCrossFade)
        transition(DrawableTransitionOptions.withCrossFade())
    transform(CircleCrop())
}

/**
 * 没有任何偏好
 */
@SuppressLint("CheckResult")
fun ImageView.loadImage(
    imgUrl: String,
    enableCrossFade: Boolean = true,
) = set(imgUrl) {
    if (enableCrossFade)
        transition(DrawableTransitionOptions.withCrossFade())
    transform(CenterCrop())
}

private fun ImageView.set(
    imgUrl: String,
    preferences: RequestBuilder<Drawable>.() -> RequestBuilder<Drawable> = { this }
) {
    setVisible()
    Glide.with(this)
        .load(imgUrl)
        .fitCenter()
        .preferences()
        .into(this)
}

private fun ImageView.setVisible() {
    visibility = View.VISIBLE
}

suspend fun toastSuspended(
    msg: String,
    cancelLast: Boolean = true,
    length: Int = Toast.LENGTH_SHORT
) = withContext(Dispatchers.Main) {
    toast(msg, cancelLast, length)
}

fun toast(msg: String, cancelLast: Boolean = true, length: Int = Toast.LENGTH_SHORT) {
    if (msg.isNotBlank() && appContext != null) {
        if (cancelLast)
            Toast(appContext).cancel()
        Toast.makeText(appContext, msg, length).show()
    }
}

fun Any?.toast(cancelLast: Boolean = true) {
    val str = this.toString()
    if (str.isNotBlank()) toast(str, cancelLast)
}


/**
 * 检查权限状态, 若未授予则先获取权限, 最后回调检查结果
 */
fun AppCompatActivity.withPermission(
    name: String = WRITE_EXTERNAL_STORAGE,
    callback: (isGranted: Boolean) -> Unit
) {
    if (isPermissionGranted(name)) {
        callback(true)
    } else {
        requestPermission(name, callback)
    }
}

/**
 * 尝试获取权限, 然后回调结果
 *
 * @param name Manifest.permission.XXX
 *
 * startActivityForResult 的简化版
 */
fun AppCompatActivity.requestPermission(
    name: String = WRITE_EXTERNAL_STORAGE,
    callback: (isGranted: Boolean) -> Unit
) = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
    callback(it)
}.launch(name)

fun Activity.isPermissionGranted(name: String): Boolean = ContextCompat.checkSelfPermission(
    this,
    name
) == PackageManager.PERMISSION_GRANTED

/**
 * 设置后可以让最近的 item 吸附至中间
 */
fun RecyclerView.setSnapHelper() {
    if (onFlingListener == null)
        PagerSnapHelper().attachToRecyclerView(this)
}

/**
 * 添加原生分割线
 */
fun RecyclerView.addLineDecoration(context: Context, orientation: Int) {
    if (itemDecorationCount == 0 && layoutManager != null)
        addItemDecoration(
            DividerItemDecoration(
                context,
                orientation
            )
        )
}

/**
 * @param cannotScrollOrientation 指定的方向
 *
 *  1: 无法往下
 *
 * -1: 无法往上
 */
fun RecyclerView.addOnLoadMoreListener_V(cannotScrollOrientation: Int, onLoad: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) // 已停止
                if (!canScrollVertically(cannotScrollOrientation)) // 在到达末尾
                    onLoad()
        }
    })
}

/**
 * @param cannotScrollOrientation 指定的方向
 *
 *  1: 无法往右
 *
 * -1: 无法往左
 */
fun RecyclerView.addOnLoadMoreListener_H(cannotScrollOrientation: Int, onLoad: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) // 已停止
                if (!canScrollHorizontally(cannotScrollOrientation)) // 在到达末尾
                    onLoad()
        }
    })
}