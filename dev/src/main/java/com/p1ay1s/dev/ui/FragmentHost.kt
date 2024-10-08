package com.p1ay1s.dev.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.p1ay1s.dev.base.throwException

class FragmentHost(
    private val viewId: Int,
    private var fragmentManager: FragmentManager,
    private var fragmentMap: LinkedHashMap<String, Fragment> // linkedHashMap 可以按 item 添加顺序排列
) {

    interface OnFragmentIndexChangedListener { // 用于通知索引改变
        fun onFragmentIndexChanged(index: String)
    }

    private var lastIndex: String? = null
    private var currentIndex: String = fragmentMap.keys.first()
        set(value) {
            indexChangedListener?.onFragmentIndexChanged(value)
            field = value
        }

    private var indexChangedListener: OnFragmentIndexChangedListener? =
        null

    /**
     * 初始化索引并添加全部 fragment , 最后显示第一个 fragment
     */
    init {
        currentIndex = fragmentMap.keys.first()

        fragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            fragmentMap.forEach { (index, fragment) ->
                add(viewId, fragment, index)
                hide(fragment)
            }
        }.commit()

        show()
    }

    /**
     * 监听 fragment 的索引
     */
    fun setOnFragmentIndexChangeListener(listener: OnFragmentIndexChangedListener) {
        indexChangedListener = listener
    }

    /**
     * 重新显示当前的 fragment , 可以在某些情况下 fragment 消失后调用
     */
    fun show() {
        navigate(currentIndex)
    }

    /**
     * 切换到某个 fragment
     *
     * 传入不存在的键直接退出函数
     */
    fun navigate(tag: String): Boolean {
        if (isIndexExisted(tag)) {
            fragmentManager.beginTransaction().apply {
                hide(getCurrentFragment())
                show(getFragment(tag))
            }.commitNow()

            if (tag == currentIndex) {
                return false
            }

            lastIndex = currentIndex
            currentIndex = tag
            return true
        } else {
            return false
        }
    }

    /**
     * 添加 fragment
     *
     * @param show 是否显示添加的 fragment
     * 如果使用了已添加的键则会覆盖对应的 fragment
     */
    fun add(index: String, fragment: Fragment, show: Boolean = true) {
        fragmentManager.beginTransaction().apply {
            fragmentMap[index] = fragment
            add(viewId, fragment, index)
            if (show) {
                hide(getCurrentFragment())

                lastIndex = currentIndex
                currentIndex = index
            } else
                hide(fragment)
        }.commitNow()
    }

    fun pop(tag: String): Boolean {
        lastIndex?.let { return pop(tag, it) }
        return false
    }

    /**
     * @param newIndex 移除后切换的索引
     */
    fun pop(tag: String, newIndex: String): Boolean {
        if (!isIndexExisted(tag) || !isIndexExisted(newIndex)) return false

        val fragment = getFragment(tag)
        navigate(newIndex)

        fragmentManager.beginTransaction().apply {
            hide(fragment)
            remove(fragment)
        }.commitNow()

        fragmentMap.remove(tag)
        return true
    }

    fun getCurrentFragment() = getFragment(currentIndex)

    fun removeAll() {
        fragmentManager.beginTransaction().apply {
            fragmentMap.forEach { (index, fragment) ->
                hide(fragment)
                remove(fragment)
                fragmentMap.remove(index)
            }
        }.commitNow()
    }

    private fun getFragment(index: String?): Fragment {
        when {
            index.isNullOrBlank() ->
                throwException("try to get a fragment with an empty key")

            !isIndexExisted(index) ->
                throwException("try to get a fragment with a not existed key")
        }
        return fragmentMap[index]!!
    }

    private fun isFragmentExisted(tag: Fragment): Boolean {
        return fragmentMap.values.any { it == tag }
    }

    private fun isIndexExisted(tag: String): Boolean {
        return fragmentMap.keys.any { it == tag }
    }
}