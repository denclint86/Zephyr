package com.p1ay1s.dev.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.p1ay1s.dev.base.getKey
import com.p1ay1s.dev.base.throwException

/**
 * 具有 fragment 管理能力的 view
 */
open class FragmentControllerView : FrameLayout {

    interface OnFragmentIndexChangedListener { // 用于通知索引改变
        fun onFragmentIndexChanged(index: String)
    }

    interface OnPassDataListener {
        fun <T> onPassData(receiverIndex: String, data: T?)
    }

    private var isInitialized = false

    protected lateinit var fragmentManager: FragmentManager
    protected lateinit var fragmentMap: LinkedHashMap<String, Fragment> // linkedHashMap 可以按 item 添加顺序排列
    protected lateinit var currentIndex: String

    private var mIndexChangedListener: OnFragmentIndexChangedListener? =
        null
    private val mPassDataListeners: MutableList<OnPassDataListener> =
        mutableListOf()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    protected fun updateIndex(newIndex: String) {
        currentIndex = newIndex
        mIndexChangedListener?.onFragmentIndexChanged(newIndex)
    }

    fun init(
        fragmentManager: FragmentManager,
        fragmentMap: LinkedHashMap<String, Fragment>
    ) {
        this.fragmentMap = fragmentMap
        this.fragmentManager = fragmentManager
        isInitialized = true
        updateIndex(fragmentMap.keys.first())
        fragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            fragmentMap.forEach { (index, fragment) ->
                add(this@FragmentControllerView.id, fragment, index)
                hide(fragment)
            }
            show(getCurrentFragment())
        }.commit()
    }

    fun setOnFragmentIndexChangeListener(listener: OnFragmentIndexChangedListener) {
        mIndexChangedListener = listener
    }

    fun addOnPassDataListener(listener: OnPassDataListener) =
        mPassDataListeners.add(listener)

    fun removeOnPassDataListener(listener: OnPassDataListener) =
        mPassDataListeners.remove(listener)

    /**
     * 可以让 child fragment 通过此 view 的实例传输数据
     */
    fun <T> passBetweenChildren(receiverIndex: String, data: T?) =
        mPassDataListeners.forEach { listener ->
            listener.onPassData(receiverIndex, data)
        }

    fun switchToFragment(target: Fragment) =
        fragmentMap.getKey(target)?.let { switchToFragment(it) }

    /**
     * 传入不存在的键直接退出函数
     */
    fun switchToFragment(target: String) {
        checkIfIsInitialized()
        if (isIndexExist(target)) {
            if (target == currentIndex) return

            fragmentManager.beginTransaction().apply {
                hide(getCurrentFragment())
                show(getFragment(target))
            }.commitNow()
            updateIndex(target)
        }
    }

    /**
     * 如果使用了已添加的键则会覆盖对应的 fragment
     */
    fun addFragment(index: String, fragment: Fragment, show: Boolean = true) {
        checkIfIsInitialized()

        fragmentManager.beginTransaction().apply {
            fragmentMap[index] = fragment
            add(this@FragmentControllerView.id, fragment, index)
        }.commitNow()

        if (show) switchToFragment(index)
    }

    fun deleteFragment(target: Fragment, defaultIndex: String, switch: Boolean = false) =
        deleteFragment(fragmentMap.getKey(target)!!, defaultIndex, switch)

    fun deleteFragment(target: String, newIndex: String, switch: Boolean = false) {
        checkIfIsInitialized()
        when {
            !isIndexExist(target) -> return
            target == currentIndex || switch -> // 当 target == currentIndex 则强制切换
                switchToFragment(newIndex)
        }
        fragmentManager.beginTransaction().apply {
            target.let {
                val fragment = fragmentMap[target]!!
                hide(fragment)
                detach(fragment)
            }
        }.commitNow()
        fragmentMap.remove(target)
    }

    fun getCurrentFragment() = getFragment(currentIndex)

    protected fun getFragment(index: String?): Fragment {
        checkIfIsInitialized()
        when {
            index.isNullOrBlank() ->
                throwException("try to get a fragment with empty key")

            !isIndexExist(index) ->
                throwException("try to get a fragment with a not existed key")
        }
        return fragmentMap[index]!!
    }

    private fun isFragmentExist(target: Fragment): Boolean {
        fragmentMap.values.forEach {
            if (it == target)
                return true
        }
        return false
    }

    private fun isIndexExist(target: String): Boolean {
        fragmentMap.keys.forEach {
            if (it == target)
                return true
        }
        return false
    }

    private fun checkIfIsInitialized() {
        if (!isInitialized) throwException("fragment controller view has not be initialized")
    }
}
