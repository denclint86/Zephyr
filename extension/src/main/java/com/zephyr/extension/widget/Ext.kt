@file:Suppress("FunctionName")

package com.zephyr.extension.widget

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.zephyr.extension.thread.runOnMain
import com.zephyr.global_values.globalContext

fun toast(msg: String, cancelLast: Boolean = true, length: Int = Toast.LENGTH_SHORT) = runOnMain {
    if (cancelLast)
        Toast(globalContext).cancel()
    Toast.makeText(globalContext, msg, length).show()
}

fun Any?.toast(cancelLast: Boolean = true) {
    val str = this.toString()
    toast(str, cancelLast)
}

fun View.setSize(size: Int) = setSize(size, size)

fun View.setSize(width: Int? = null, height: Int? = null) = updateLayoutParams {
    width?.let { this.width = it }
    height?.let { this.height = it }
}

fun View.setMargins_H(margin: Int) {
    setMargins(start = margin, end = margin)
}

fun View.setMargins_V(margin: Int) {
    setMargins(top = margin, bottom = margin)
}

fun View.setMargins(margin: Int) {
    setMargins(margin, margin, margin, margin)
}

fun View.setMargins(
    start: Int? = null,
    end: Int? = null,
    top: Int? = null,
    bottom: Int? = null
) {
    val lp = layoutParams
    (lp as? ViewGroup.MarginLayoutParams)?.run {
        start?.let { leftMargin = it }
        end?.let { rightMargin = it }
        top?.let { topMargin = it }
        bottom?.let { bottomMargin = it }
    } ?: return
    layoutParams = lp
}

/**
 * 设置后可以让最近的 item 吸附至中间
 */
fun RecyclerView.setSnapHelper() {
    if (onFlingListener != null) return
    PagerSnapHelper().attachToRecyclerView(this)
}

/**
 * 添加原生分割线
 */
fun RecyclerView.addLineDecoration(context: Context, orientation: Int) {
    if (itemDecorationCount != 0 || layoutManager == null) return
    addItemDecoration(
        DividerItemDecoration(
            context,
            orientation
        )
    )
}

fun RecyclerView.removeOnLoadMoreListener(listener: RecyclerView.OnScrollListener) {
    removeOnScrollListener(listener)
}

/**
 * @param orientation 指定的方向
 */
fun RecyclerView.addOnLoadMoreListener_V(
    orientation: VerticalCannotScroll,
    onLoad: () -> Unit
): RecyclerView.OnScrollListener {
    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) // 已停止
                if (!canScrollVertically(orientation.v)) // 在到达末尾
                    onLoad()
        }
    }
    addOnScrollListener(listener)
    return listener
}

/**
 * @param orientation 指定的方向
 */
fun RecyclerView.addOnLoadMoreListener_H(
    orientation: HorizontalCannotScroll,
    onLoad: () -> Unit
): RecyclerView.OnScrollListener {
    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) // 已停止
                if (!canScrollHorizontally(orientation.v)) // 在到达末尾
                    onLoad()
        }
    }
    addOnScrollListener(listener)
    return listener
}

enum class VerticalCannotScroll(val v: Int) {
    Up(-1), Down(1)
}

enum class HorizontalCannotScroll(val v: Int) {
    Left(-1), Right(1)
}