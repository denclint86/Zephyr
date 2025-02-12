package com.zephyr.base.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.zephyr.base.log.logE
import kotlin.collections.set

/**
 * 具有 fragment 管理能力的 view
 *
 * 如果你苦于 navController 的重走生命周期问题,
 * 但又不想自己写子类可以用这个
 */
open class FragmentHostView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var TAG: String = this::class.java.simpleName

    fun interface OnHostChangeListener {
        fun onChanged(newHost: FragmentHost?, newIndex: Int)
    }

    private var listener: OnHostChangeListener? = null

    var fragmentManager: FragmentManager? = null
        set(value) {
            _map.values.forEach { host ->
                value?.let { host.fragmentManager = it }
            }
            field = value
        }
        get() {
            if (field?.isDestroyed == true) {
                field = null
            }
            return field
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

    fun getActiveHost() = getHost(activeIndex)
    private fun getLastHost() = getHost(lastIndex)
    private fun getHost(index: Int?) = _map[index]

    private fun restoreStack() = _map.values.forEach { host ->
        host.recreateFragments(fragmentManager!!)
    }

    fun setOnHostChangeListener(l: OnHostChangeListener?) {
        listener = l
    }

    fun restore(map: HashMap<Int, FragmentHost>, targetHost: Int) {
        this._map = map
        restoreStack()
        switchHost(targetHost)
    }

    private fun createHost(): FragmentHost {
        return FragmentHost(id).apply {
            fragmentManager = this@FragmentHostView.fragmentManager
        }
    }

    fun addHost(index: Int, tag: String, clazz: Class<out Fragment>): FragmentHost {
        return createHost().apply {
            pushFragment(tag, clazz)
            _map[index] = this
            switchHost(index)
        }
    }

    fun addHost(index: Int, tag: String, fragment: Fragment): FragmentHost {
        return createHost().apply {
            pushFragment(tag, fragment)
            _map[index] = this
            switchHost(index)
        }
    }

    fun addHost(index: Int): FragmentHost {
        return createHost().apply {
            _map[index] = this
            switchHost(index)
        }
    }

    fun switchHost(index: Int, enter: Int = 0, exit: Int = 0) {
        if (_map.keys.any { it == index }) {
            var host: FragmentHost? = null
            fragmentManager?.beginTransaction()?.apply {
                setCustomAnimations(enter, exit)
                getActiveHost()?.hideTransaction(this)
                getHost(index)?.let {
                    host = it
                    it.showTransaction(this)
                }

            }?.commitNow()
            activeIndex = index
            logE(TAG, "切换 host 从 $lastIndex 到 $activeIndex")
            listener?.onChanged(host, index)
        }
    }
}