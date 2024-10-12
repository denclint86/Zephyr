package com.p1ay1s.base.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.p1ay1s.base.extension.throwException

/**
 * 基于 fragmentManager 的工具类
 *
 * 仿照 NavController 的部分功能实现,
 * 提供更友好的 fragment 的生命周期管理
 *
 * 索引改用 Int , 建议用魔法变量访问
 */
open class FragmentHost(
    private val viewId: Int,
    var fragmentManager: FragmentManager,
    private var fragmentMap: LinkedHashMap<Int, Class<out Fragment>> // linkedHashMap 可以按 item 添加顺序排列
) {

    /**
     * 用于通知索引改变的监听器
     */
    interface OnIndexChangeListener {
        fun onIndexChanged(index: Int)
    }

    private var currentIndex: Int = fragmentMap.keys.first()
        set(value) { // 赋值前通知索引改变
            indexChangedListener?.onIndexChanged(value)
            field = value
        }

    private var indexChangedListener: OnIndexChangeListener? =
        null

    /**
     * 初始化索引并添加全部 fragment,
     * 最后显示第一个添加的 fragment
     */
    init {
        addAll()
    }

    /**
     * 将 map 的全部键值加入到 fragmentManager
     */
    fun addAll(map: LinkedHashMap<Int, Class<out Fragment>>? = null) {
        map?.let { fragmentMap.putAll(it) }
        fragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            fragmentMap.forEach { (index, fragment) ->
                val f = fragment.getDeclaredConstructor().newInstance()
                this.add(viewId, f, index.toString())
                this.hide(f)
            }
        }.commit()
        fragmentManager.executePendingTransactions()
    }

    /**
     * 设置加索引监听器
     */
    fun setOnIndexChangeListener(listener: OnIndexChangeListener) {
        indexChangedListener = listener
    }

    fun removeOnIndexChangeListener() {
        indexChangedListener = null
    }

    fun findFragment(tag: Int): Fragment? {
        return fragmentManager.findFragmentByTag(tag.toString())
    }

    /**
     * 显示当前的 fragment
     *
     * @param enter 进入动画
     */
    fun show(enter: Int = 0) {
        fragmentManager.beginTransaction().apply {
            setCustomAnimations(enter, 0)
            show(getCurrentFragment())
        }.commit()
        fragmentManager.executePendingTransactions()
    }

    /**
     * 隐藏当前的 fragment
     *
     * @param exit 退出动画
     */
    fun hide(exit: Int = 0) {
        fragmentManager.beginTransaction().apply {
            setCustomAnimations(0, exit)
            hide(getCurrentFragment())
        }.commit()
        fragmentManager.executePendingTransactions()
    }

    /**
     * 切换到某个 fragment
     *
     * @param enter 进入动画
     * @param exit 退出动画
     *
     * @return 是否切换到了目标 fragment,
     * 当前索引已经是目标索引时返回假.
     * 传入不存在的键直接退出函数
     */
    fun navigate(tag: Int, enter: Int = 0, exit: Int = 0): Boolean {
        if (isIndexExisted(tag)) {
            if (tag == currentIndex) return false

            fragmentManager.beginTransaction().apply {
                setCustomAnimations(enter, exit)
                hide(getCurrentFragment())
                show(getFragment(tag))
            }.commit()
            fragmentManager.executePendingTransactions()
            currentIndex = tag
            return true
        } else {
            return false
        }
    }

    /**
     * 添加并显示 fragment
     *
     * @param enter 进入动画
     * @param exit 退出动画
     */
    fun add(index: Int, fragment: Class<out Fragment>, enter: Int = 0, exit: Int = 0) {
        add(index, fragment, false)
        navigate(index, enter, exit)
    }

    /**
     * 添加 fragment
     *
     * @param show 是否显示添加的 fragment,
     * 如果使用了已添加的索引则会覆盖对应的 fragment
     */
    fun add(index: Int, fragment: Class<out Fragment>, show: Boolean) {
        fragmentManager.beginTransaction().apply {
            if (isIndexExisted(index)) {
                runCatching {
                    val oldFragment = getFragment(index)
                    hide(oldFragment)
                    remove(oldFragment)
                }
            }

            fragmentMap[index] = fragment
            val f = fragment.getDeclaredConstructor().newInstance()
            this.add(viewId, f, index.toString()) // 就尼玛无语, 在这无限递归了
            if (show) {
                hide(getCurrentFragment())

                currentIndex = index
            } else
                hide(f)
        }.commit()
        fragmentManager.executePendingTransactions()
    }

    /**
     * 移除一个未显示的 fragment
     */
    fun remove(tag: Int): Boolean {
        if (tag == currentIndex) return false

        val fragment = getFragment(tag)
        fragmentManager.beginTransaction().apply {
            hide(fragment)
            remove(fragment)
        }.commit()
        fragmentManager.executePendingTransactions()

        fragmentMap.remove(tag)
        return true
    }

    /**
     * 移除指定的 fragment
     *
     * @param newIndex 移除后切换的索引
     */
    fun pop(tag: Int, newIndex: Int, enter: Int = 0, exit: Int = 0): Boolean {
        if (!isIndexExisted(tag) || !isIndexExisted(newIndex)) return false

        if (!navigate(newIndex, enter, exit)) return false

        return remove(tag)
    }

    fun getCurrentFragment() = getFragment(currentIndex)

    fun removeAll() {
        fragmentManager.beginTransaction().apply {
            fragmentMap.forEach { (index, fragment) ->
                findFragment(index)?.let { this.hide(it) }
                findFragment(index)?.let { this.remove(it) }
                fragmentMap.remove(index)
            }
        }.commit()
        fragmentManager.executePendingTransactions()
    }

    private fun getFragment(index: Int): Fragment {
        if (!isIndexExisted(index))
            throwException("try to get a fragment with a not existed key")
        val f = findFragment(index) ?: fragmentMap[index]!!.getDeclaredConstructor().newInstance()
        return f
    }

    private fun isIndexExisted(tag: Int): Boolean {
        return fragmentMap.keys.any { it == tag }
    }
}