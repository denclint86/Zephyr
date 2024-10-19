package com.p1ay1s.base.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * 具有 fragment 管理能力的 view
 *
 * 如果你苦于 navController 的重走生命周期问题,
 * 但又不想自己写子类可以用这个
 */
class FragmentHostView : FrameLayout {
    private var _fragmentHost: FragmentHost? = null
    val fragmentHost: FragmentHost
        get() = _fragmentHost
            ?: throw IllegalStateException("have to create FragmentHost first")

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun create(
        fragmentManager: FragmentManager,
        map: LinkedHashMap<Int, Class<out Fragment>>
    ): FragmentHost {
        _fragmentHost = FragmentHost(id, fragmentManager, map)
        return fragmentHost
    }

    fun restore(fragmentHost: FragmentHost, fragmentManager: FragmentManager) {
        if (_fragmentHost == null)
            _fragmentHost = fragmentHost
        _fragmentHost?.fragmentManager = fragmentManager
        _fragmentHost?.addAll()
    }

    fun release() {
        _fragmentHost?.removeAll()
        _fragmentHost = null
    }
}