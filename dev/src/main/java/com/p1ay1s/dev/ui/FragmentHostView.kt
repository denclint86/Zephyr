package com.p1ay1s.dev.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * 具有 fragment 管理能力的 view
 *
 * 感觉 navController 还是有很多不适合我的地方,
 * 遂自己实现一个
 */
class FragmentHostView : FrameLayout {
    private var _fragmentHost: FragmentHost? = null
    val fragmentHost: FragmentHost
        get() = _fragmentHost
            ?: throw IllegalStateException("have to call FragmentControllerView.init(...) first")

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun init(fragmentManager: FragmentManager, fragmentMap: LinkedHashMap<String, Fragment>) {
        _fragmentHost = FragmentHost(id, fragmentManager, fragmentMap)
    }
}
