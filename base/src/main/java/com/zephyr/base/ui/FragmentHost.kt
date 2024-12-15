package com.zephyr.base.ui

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.util.Stack

/**
 * 在 view 以及它的子 view 中寻找并返回一个 FragmentHost 实例
 */
fun Fragment.findHost(): FragmentHost? {
    var view = view
    var parent = view?.parent

    while (parent != null) {
        if (parent is FragmentHostView) {
            return parent.getActiveHost()
        }
        view = parent as? View // as? 如果转换失败则变为 null
        parent = view?.parent
    }
    return null
}

/**
 * 负责管理一个页面中的 fragment
 *
 * 每一个实例都是 fragment 返回栈
 */
open class FragmentHost(
    private val viewId: Int,
) : Stack<Pair<Int, Class<out Fragment>>>() {

    lateinit var fragmentManager: FragmentManager

    private fun witFragment(tag: Int, callback: (Fragment) -> Unit) {
        val f = findFragment(tag)
        f?.let { callback(it) }
    }

    /**
     * 尝试在 fragment manager 获取 fragment
     */
    private fun findFragment(tag: Int): Fragment? {
        if (fragmentManager.isDestroyed) return null
        return fragmentManager.findFragmentByTag(tag.toString())
    }

    // transaction 片段
    fun showTransaction(transaction: FragmentTransaction): FragmentTransaction {
        transaction.apply {
            peek()?.let {
                witFragment(it.first) { fragment ->
                    show(fragment)
                }
            }
            return this
        }
    }

    // transaction 片段
    fun hideTransaction(transaction: FragmentTransaction): FragmentTransaction {
        transaction.apply {
            peek()?.let {
                witFragment(it.first) { fragment ->
                    hide(fragment)
                }
            }
            return this
        }
    }

    // transaction 片段
    private fun popOnceTransaction(transaction: FragmentTransaction): FragmentTransaction {
        transaction.apply {
            pop()?.run {
                witFragment(first) { fragment ->
                    hide(fragment)
                    remove(fragment)
                }
            }
            return this
        }
    }

    /**
     * @param enter 进入动画
     */
    fun show(enter: Int = 0) {
        if (fragmentManager.isDestroyed) return
        fragmentManager.beginTransaction().apply {
            setCustomAnimations(enter, 0)
            showTransaction(this)
        }.commit()
        fragmentManager.executePendingTransactions()
    }

    /**
     * @param exit 退出动画
     */
    fun hide(exit: Int = 0) {
        if (fragmentManager.isDestroyed) return
        fragmentManager.beginTransaction().apply {
            setCustomAnimations(0, exit)
            hideTransaction(this)
        }.commit()
        fragmentManager.executePendingTransactions()
    }

    /**
     * 弹出栈顶的 fragment
     */
    fun popFragment(enter: Int = 0, exit: Int = 0): Boolean {
        if (!empty() && !fragmentManager.isDestroyed) {
            fragmentManager.beginTransaction().apply {
                setCustomAnimations(enter, exit)
                popOnceTransaction(this)
                showTransaction(this)
            }.commit()
            fragmentManager.executePendingTransactions()
            return true
        }
        return false
    }

    /**
     * 压入 fragment 到栈顶
     */
    fun pushFragment(tag: Int, fragmentClazz: Class<out Fragment>, enter: Int = 0, exit: Int = 0) {
        createFragmentInstance(fragmentClazz)?.let { pushFragment(tag, it, enter, exit) }
    }

    fun pushFragment(tag: Int, fragment: Fragment, enter: Int = 0, exit: Int = 0) {
        if (fragmentManager.isDestroyed) return
        fragmentManager.beginTransaction().apply {
            setCustomAnimations(enter, exit)
            getPeekFragment()?.let { hide(it) }
            push(Pair(tag, fragment::class.java))
            add(viewId, fragment, tag.toString())
        }.commit()
        fragmentManager.executePendingTransactions()
    }

    /**
     * 导航到某个已入栈 fragment
     */
    fun navigateFragment(tag: Int, enter: Int = 0, exit: Int = 0): Boolean {
        if (!isPushed(tag) || fragmentManager.isDestroyed) return false
        fragmentManager.beginTransaction().apply {
            setCustomAnimations(enter, exit)
            while (tag != peek().first) {
                popOnceTransaction(this)
            }
            showTransaction(this)
        }.commit()
        fragmentManager.executePendingTransactions()
        return true
    }

    /**
     * 重新添加栈内 fragment 至 fragment manager
     */
    fun recreateFragments(newManager: FragmentManager = fragmentManager) {
        fragmentManager = newManager
        newManager.beginTransaction().apply {
            forEach { pair ->
                createFragmentInstance(pair.second)?.let {
                    add(viewId, it, pair.first.toString())
                    hide(it)
                }
            }
        }.commit()
        newManager.executePendingTransactions()
    }

    private fun isPushed(tag: Int) = searchByTag(tag) != -1

    private fun searchByTag(tag: Int): Int {
        if (empty()) return -1
        forEachIndexed { index, pair ->
            if (pair.first == tag) return index
        }
        return -1
    }

    fun getPeekPair(): Pair<Int, Class<out Fragment>>? {
        if (empty()) return null
        return peek()
    }

    private fun getPeekFragment(): Fragment? {
        if (empty()) return null
        return findFragment(peek().first)
    }

    /**
     * 尝试用无参的构造函数实例化并返回一个 fragment
     *
     * 没有无参的构造函数会崩溃, 不懂为什么
     */
    private fun createFragmentInstance(clazz: Class<out Fragment>): Fragment? {
        val f: Fragment?
        try {
            f = clazz.getDeclaredConstructor().newInstance()
            return f
        } catch (_: Exception) {
            return null
        }
    }
}