package com.p1ay1s.dev.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.p1ay1s.dev.base.throwException

/**
 * 基于 fragmentManager 的工具类
 *
 * 仿照 NavController 的部分功能实现,
 * 提供更友好的 fragment 的生命周期管理
 */
class FragmentHost(
    private val viewId: Int,
    var fragmentManager: FragmentManager,
    private var fragmentMap: LinkedHashMap<String, Fragment> // linkedHashMap 可以按 item 添加顺序排列
) {
    /**
     * 用于通知索引改变的监听器
     */
    interface OnFragmentIndexChangedListener {
        fun onFragmentIndexChanged(index: String)
    }

    private var lastIndex: String? = null
    private var currentIndex: String = fragmentMap.keys.first()
        set(value) { // 赋值前通知索引改变
            indexChangedListener?.onFragmentIndexChanged(value)
            field = value
        }

    private var indexChangedListener: OnFragmentIndexChangedListener? =
        null

    /**
     * 初始化索引并添加全部 fragment,
     * 最后显示第一个添加的 fragment
     */
    init {
        currentIndex = fragmentMap.keys.first()
        addAll()
        fragmentManager.executePendingTransactions()
    }

    /**
     * 将 map 的全部键值加入到 fragmentManager
     */
    fun addAll(map: LinkedHashMap<String, Fragment>? = null) {
        map?.let { fragmentMap.putAll(it) }
        fragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            fragmentMap.forEach { (index, fragment) ->
                add(viewId, fragment, index)
                hide(fragment)
            }
        }.commit()
    }

    /**
     * 设置加索引监听器
     */
    fun setOnFragmentIndexChangeListener(listener: OnFragmentIndexChangedListener) {
        indexChangedListener = listener
    }

    /**
     * 显示当前的 fragment
     */
    fun show() {
        fragmentManager.beginTransaction().apply {
            show(getCurrentFragment())
        }.commitNow()
    }

    /**
     * 隐藏当前的 fragment
     */
    fun hide() {
        fragmentManager.beginTransaction().apply {
            hide(getCurrentFragment())
        }.commitNow()
    }

    /**
     * 切换到某个 fragment
     *
     * @return 是否切换到了目标 fragment,
     * 当前索引已经是目标索引时返回假.
     * 传入不存在的键直接退出函数
     */
    fun navigate(tag: String): Boolean {
        if (isIndexExisted(tag)) {
            if (tag == currentIndex) return false

            fragmentManager.beginTransaction().apply {
                hide(getCurrentFragment())
                show(getFragment(tag))
            }.commitNow()

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
     * @param show 是否显示添加的 fragment,
     * 如果使用了已添加的索引则会覆盖对应的 fragment
     */
    fun add(index: String, fragment: Fragment, show: Boolean = true) {
        fragmentManager.beginTransaction().apply {
            if (isIndexExisted(index)) {
                runCatching {
                    val oldFragment = getFragment(index)
                    hide(oldFragment)
                    remove(oldFragment)
                }
            }

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

    /**
     * 移除当前显示的 fragment
     */
    fun pop(tag: String): Boolean {
        lastIndex?.let { return pop(tag, it) }
        return false
    }

    /**
     * 移除指定的 fragment
     *
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