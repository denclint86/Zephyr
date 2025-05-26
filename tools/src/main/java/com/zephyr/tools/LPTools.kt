package com.zephyr.tools

import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams


fun View.setSize(size: Int) = setSize(size, size)

fun View.setSize(width: Int? = null, height: Int? = null) = updateLayoutParams {
    width?.let { this.width = it }
    height?.let { this.height = it }
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

fun View.setViewInsets(block: ViewGroup.MarginLayoutParams.(Insets) -> Unit) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            block(insets) // 将 insets 传递给 block
        }
        WindowInsetsCompat.CONSUMED
    }
}

fun View.parentIs(parentView: View): Boolean {
    if (this == parentView) return true

    var currentParent = parent
    while (currentParent != null) {
        if (currentParent == parentView) {
            return true
        }
        currentParent = (currentParent as? View)?.parent
    }
    return false
}

fun View.findViewAtPoint(x: Float, y: Float): View? {
    // 遍历 View 树, 找到点击位置的 View
    val rootView = this
    return findViewAtPointRecursive(rootView, x, y)
}

private fun findViewAtPointRecursive(view: View, x: Float, y: Float): View? {
    if (view !is ViewGroup) {
        return if (view.isClickable && isPointInsideView(x, y, view)) view else null
    }
    // 从上到下遍历子 View
    for (i in view.childCount - 1 downTo 0) {
        val child = view.getChildAt(i)
        if (isPointInsideView(x, y, child)) {
            return findViewAtPointRecursive(child, x, y) ?: child
        }
    }
    return if (view.isClickable && isPointInsideView(x, y, view)) view else null
}

private fun isPointInsideView(x: Float, y: Float, view: View): Boolean {
    val location = IntArray(2)
    view.getLocationOnScreen(location)
    val left = location[0]
    val top = location[1]
    val right = left + view.width
    val bottom = top + view.height
    return x >= left && x <= right && y >= top && y <= bottom && view.isShown
}