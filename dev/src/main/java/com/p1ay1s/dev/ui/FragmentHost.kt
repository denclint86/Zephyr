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
            show(getCurrentFragment())
        }.commit()
    }

    /**
     * 监听 fragment 的索引
     */
    fun setOnFragmentIndexChangeListener(listener: OnFragmentIndexChangedListener) {
        indexChangedListener = listener
    }

//     fun navigate(tag: Fragment) {
//        fragmentMap.getKey(tag)?.let { switchToFragment(it) }
//    }

    /**
     * 切换到某个 fragment
     *
     * 传入不存在的键直接退出函数
     */
    fun navigate(tag: String) {
        if (isIndexExisted(tag)) {
            if (tag == currentIndex) return

            fragmentManager.beginTransaction().apply {
                hide(getCurrentFragment())
                show(getFragment(tag))
            }.commitNow()

            currentIndex = tag
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
        }.commitNow()

        if (show) navigate(index)
    }

//     fun remove(tag: Fragment, defaultIndex: String, switch: Boolean) =
//        deleteFragment(fragmentMap.getKey(tag)!!, defaultIndex, switch)

    /**
     * 移除 fragment
     *
     * @param newIndex 移除后切换的索引
     * @param switch 是否选择切换(移除当前显示的 fragment 则会强制切换)
     */
    fun remove(tag: String, newIndex: String, switch: Boolean = false) {
        if (!isIndexExisted(tag)) return

        if (tag == currentIndex || switch) // 当 tag == currentIndex 则强制切换
            navigate(newIndex)
        fragmentManager.beginTransaction().apply {
            tag.let {
                val fragment = fragmentMap[tag]!!
                hide(fragment)
                detach(fragment)
            }
        }.commitNow()
        fragmentMap.remove(tag)
    }

    fun getCurrentFragment() = getFragment(currentIndex)

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