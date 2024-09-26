package com.p1ay1s.dev.ui

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

open class PreloadLayoutManager(
    context: Context,
    orientation: Int,
    private val size: Int = 4,
    reverseLayout: Boolean = false
) : LinearLayoutManager(context, orientation, reverseLayout) {

    /**
     * calculateExtraLayoutSpace 方法可以用来增加 RecyclerView 预留的额外空间,
     * 有助于提前加载屏幕外的 Item,
     * 避免滑动过程中的卡顿
     *
     * 通过重写 calculateExtraLayoutSpace 方法来返回额外的空间大小,
     * 以便 RecyclerView 在滑动过程中预加载屏幕外的 Item
     */
    override fun calculateExtraLayoutSpace(state: RecyclerView.State, extraLayoutSpace: IntArray) {
        super.calculateExtraLayoutSpace(state, extraLayoutSpace)
        // 设置额外的布局空间，可以根据需要动态计算
        extraLayoutSpace[0] = 400
        extraLayoutSpace[1] = 400
    }

    /**
     * collectAdjacentPrefetchPositions 方法是 RecyclerView 中的一个保护方法,
     * 用于收集与给定位置相邻的预取位置
     * 这个方法主要用于 RecyclerView 的预取机制,
     * 用于在滑动过程中预取与当前位置相邻的 Item 数据,
     * 提高滑动的流畅度
     *
     * 你可以在自定义 LayoutManager 中重写 collectAdjacentPrefetchPositions 方法来实现相邻位置的预取逻辑
     */
    override fun collectAdjacentPrefetchPositions(
        dx: Int,
        dy: Int,
        state: RecyclerView.State?,
        layoutPrefetchRegistry: LayoutPrefetchRegistry
    ) {
        super.collectAdjacentPrefetchPositions(dx, dy, state, layoutPrefetchRegistry)
        // 根据滑动方向( dx, dy )收集相邻的预取位置
        val firstPosition = findFirstVisibleItemPosition()
        val lastPosition = findLastVisibleItemPosition()
        // 预加载屏幕外的 n 个item
        if (dy > 0) {
            // 向下滑动，预取下面的 Item 数据
            for (i in (firstPosition + 1) until
                    if ((state?.itemCount ?: 0) > (lastPosition + size))
                        lastPosition + size
                    else state?.itemCount ?: 0
            ) {
                layoutPrefetchRegistry.addPosition(i, 0)
            }
        } else {
            // 向上滑动，预取上面的 Item 数据
            for (i in firstPosition - 1 downTo
                    if (firstPosition - size > 0) firstPosition - size
                    else 0
            ) {
                layoutPrefetchRegistry.addPosition(i, 0)
            }
        }
    }
}