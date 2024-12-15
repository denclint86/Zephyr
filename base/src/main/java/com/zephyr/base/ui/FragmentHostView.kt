package com.zephyr.base.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager

/**
 * 具有 fragment 管理能力的 view
 *
 * 如果你苦于 navController 的重走生命周期问题,
 * 但又不想自己写子类可以用这个
 */
class FragmentHostView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    interface OnHostChangeListener {
        fun onHostChanged(newHost: FragmentHost?, newIndex: Int)
    }

    private var listener: OnHostChangeListener? = null

    var fragmentManager: FragmentManager? = null
        set(value) {
            _map.values.forEach { host ->
                value?.let { host.fragmentManager = it }
            }
            field = value
        }

    private var _map: HashMap<Int, FragmentHost> = hashMapOf()
    val map
        get() = _map

    private var lastIndex: Int? = null
    private var activeIndex: Int? = null
        set(value) {
            lastIndex = field
            field = value
        }

    fun getActiveHost() = _map[activeIndex]
    private fun getLastHost() = _map[lastIndex]

    private fun restoreStack() = _map.values.forEach { host ->
        host.recreateFragments(fragmentManager!!)
    }

    fun setOnHostChangeListener(l: OnHostChangeListener) {
        listener = l
    }

    fun restore(map: HashMap<Int, FragmentHost>, targetHost: Int) {
        this._map = map
        restoreStack()
        switchHost(targetHost)
    }

    fun addHost(index: Int, action: FragmentHost.() -> Unit): FragmentHost {
        FragmentHost(id).let {
            it.fragmentManager = fragmentManager!!
            it.action()
            _map[index] = it
            switchHost(index)
            return it
        }
    }

    fun switchHost(index: Int, enter: Int = 0, exit: Int = 0) {
        if (_map.keys.any { it == index }) {
            activeIndex = index
            fragmentManager?.beginTransaction()?.apply {
                setCustomAnimations(enter, exit)
                getLastHost()?.hideTransaction(this)
                getActiveHost()?.let {
                    listener?.onHostChanged(it, index)
                    it.showTransaction(this)
                }
            }?.commit()
            fragmentManager?.executePendingTransactions()
        }
    }
}