package com.zephyr.tools

import android.content.Context
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewHelper private constructor(private val recyclerView: RecyclerView) {
    companion object {
        /**
         * 将 Helper 绑定到指定的 RecyclerView
         */
        fun attachTo(recyclerView: RecyclerView): RecyclerViewHelper {
            return RecyclerViewHelper(recyclerView)
        }
    }

    /**
     * 设置 PagerSnapHelper, 使最近的 item 吸附至中间
     */
    fun setSnapHelper() {
        recyclerView.let {
            if (it.onFlingListener != null) return
            PagerSnapHelper().attachToRecyclerView(it)
        }
    }

    /**
     * 添加原生分割线
     */
    fun addLineDecoration(context: Context, orientation: Int) {
        recyclerView.let {
            if (it.itemDecorationCount != 0 || it.layoutManager == null) return
            it.addItemDecoration(DividerItemDecoration(context, orientation))
        }
    }

    /**
     * 移除加载更多的监听器
     */
    fun removeOnLoadMoreListener(listener: RecyclerView.OnScrollListener) {
        recyclerView.removeOnScrollListener(listener)
    }

    /**
     * 添加触底/触边加载监听器
     * @return 返回监听器, 以便后续移除
     */
    fun addOnLoadMoreListener(dir: Dir, onLoadMore: () -> Unit): RecyclerView.OnScrollListener {
        return when (dir) {
            Dir.UP, Dir.DOWN -> addOnVerticalLoadMoreListener(dir, onLoadMore)
            Dir.LEFT, Dir.RIGHT -> addOnHorizontalLoadMoreListener(dir, onLoadMore)
        }
    }

    private fun addOnHorizontalLoadMoreListener(
        dir: Dir,
        onLoadMore: () -> Unit
    ): RecyclerView.OnScrollListener {
        val listener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!recyclerView.canScrollHorizontally(dir.value)) {
                        onLoadMore()
                    }
                }
            }
        }
        recyclerView.addOnScrollListener(listener)
        return listener
    }

    private fun addOnVerticalLoadMoreListener(
        dir: Dir,
        onLoadMore: () -> Unit
    ): RecyclerView.OnScrollListener {
        val listener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!recyclerView.canScrollVertically(dir.value)) {
                        onLoadMore()
                    }
                }
            }
        }
        recyclerView.addOnScrollListener(listener)
        return listener
    }

    enum class Dir(val value: Int) {
        UP(-1),
        DOWN(1),
        LEFT(-1),
        RIGHT(1)
    }
}