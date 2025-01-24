package com.zephyr.base.ui

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.zephyr.base.log.logE

/**
 * 在 view 以及它的子 view 中寻找并返回一个 FragmentHost 实例
 */
fun Fragment.findHost(): FragmentHost? {
    val host = view.findHost()
    val pair = host?.findPair(this::class.java)
    if (pair?.first == tag) // 防止一些情况下压入 fragment 到错误的栈中
        return host

    logE("", "${pair.toString()} ${this::class.java} $tag")
    return null
}

fun View?.findHost(): FragmentHost? {
    var view: View?
    var parent = this?.parent

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
) : PairStack<String, Class<out Fragment>>() {
    private val TAG: String = this::class.java.simpleName

    var fragmentManager: FragmentManager? = null
        get() {
            if (field?.isDestroyed == true) {
                field = null
            }
            return field
        }

    private fun beginTransaction(block: FragmentTransaction.() -> Unit) =
        fragmentManager?.beginTransaction()?.apply(block)

    /**
     * 尝试在 fragment manager 获取 fragment 实例
     */
    @JvmName("findFragment1")
    private fun findFragment(pair: Pair<String, Class<out Fragment>>?): Fragment? {
        return findFragment(pair?.first)
    }

    /**
     * 尝试在 fragment manager 获取 fragment 实例
     */
    @JvmName("findFragment3")
    private fun findFragment(clazz: Class<out Fragment>?): Fragment? {
        val tag = findPair(clazz)?.first
        return findFragment(tag)
    }

    /**
     * 尝试在 fragment manager 获取 fragment 实例
     */
    @JvmName("findFragment3")
    private fun findFragment(tag: String?): Fragment? {
        val clazz = findPair(tag)?.second
        val f = fragmentManager?.findFragmentByTag(tag.toString())
            ?: clazz?.let { createFragmentInstance(it) }
        if (f == null)
            logE(TAG, "无法获取 tag 为 $tag 的 fragment")
        return f
    }

    /**
     * 在 beginTransaction 后调用, 显示当前 fragment host 中栈顶的 fragment
     */
    fun showTransaction(transaction: FragmentTransaction): FragmentTransaction {
        transaction.apply {
            findFragment(peek())?.let {
                show(it)
            }
            return this
        }
    }

    /**
     * 在 beginTransaction 后调用, 隐藏当前 fragment host 中栈顶的 fragment
     */
    fun hideTransaction(
        transaction: FragmentTransaction
    ): FragmentTransaction {
        transaction.apply {
            findFragment(peek())?.let { hide(it) }
//            // 如果通过 pair 找不到, 则隐藏所有可见的 fragment
//            fragmentManager?.fragments?.forEach { fragment ->
//                if (fragment.isVisible)
//                    hide(fragment)
//            }
            return this
        }
    }

    /**
     * 在 beginTransaction 后调用, 弹出当前 fragment host 中栈顶的 fragment
     */
    private fun popOnceTransaction(transaction: FragmentTransaction): FragmentTransaction {
        transaction.apply {
            findFragment(pop())?.let {
                hide(it)
                remove(it)
            }
            return this
        }
    }

    /**
     * 使用当前持有的 fragmentManager 显示 fragment
     * @param enter 进入动画
     */
    fun show(enter: Int = 0) {
        beginTransaction {
            setCustomAnimations(enter, 0)
            showTransaction(this)
        }?.commitNow()
    }

    /**
     * 使用当前持有的 fragmentManager 隐藏 fragment
     * @param exit 退出动画
     */
    fun hide(exit: Int = 0) {
        beginTransaction {
            setCustomAnimations(0, exit)
            hideTransaction(this)
        }?.commitNow()
    }

    /**
     * 弹出栈顶的 fragment
     */
    fun popFragment(enter: Int = 0, exit: Int = 0): Boolean {
        if (!empty())
            return false

        beginTransaction {
            setCustomAnimations(enter, exit)
            popOnceTransaction(this)
            showTransaction(this)
        }?.commitNow()
        return true
    }

    /**
     * 压入 fragment 到栈顶
     */
    fun pushFragment(
        tag: String,
        clazz: Class<out Fragment>,
        enter: Int = 0,
        exit: Int = 0
    ): Fragment? {
        val f = createFragmentInstance(clazz)
        f?.let { pushFragment(tag, it, enter, exit) }
        return f
    }

    fun pushFragment(tag: String, fragment: Fragment, enter: Int = 0, exit: Int = 0) {
        beginTransaction {
            setCustomAnimations(enter, exit)
            getPeekFragment()?.let { hide(it) }
            push(Pair(tag, fragment::class.java))
            add(viewId, fragment, tag)
        }?.commitNow()
    }

    /**
     * 导航到某个已入栈 fragment (并弹出所有在其之上的 fragment)
     */
    fun navigateFragment(tag: String, enter: Int = 0, exit: Int = 0): Boolean {
        if (!isPushed(tag) || fragmentManager == null) return false
        beginTransaction {
            setCustomAnimations(enter, exit)
            while (tag != peek()?.first) {
                popOnceTransaction(this)
            }
            showTransaction(this)
        }?.commitNow()
        return true
    }

    /**
     * 重新添加栈内 fragment 至 fragment manager
     */
    fun recreateFragments(newManager: FragmentManager? = fragmentManager) {
        fragmentManager = newManager
        beginTransaction {
            forEach { pair ->
                createFragmentInstance(pair.second)?.let {
                    add(viewId, it, pair.first)
                    hide(it)
                }
            }
        }?.commitNow()
    }

    private fun isPushed(tag: String) = searchByTag(tag) != -1

    private fun searchByTag(tag: String): Int {
        if (empty()) return -1
        forEachIndexed { index, pair ->
            if (pair.first == tag) return index
        }
        logE(TAG, "tag 为 $tag 的 fragment 无法被找到")
        return -1
    }

    private fun getPeekFragment(): Fragment? {
        if (empty()) return null
        return findFragment(peek()?.first)
    }

    /**
     * 尝试用无参的构造函数实例化并返回一个 fragment
     *
     * 没有无参的构造函数会崩溃, 不懂为什么
     */
    private fun createFragmentInstance(
        clazz: Class<out Fragment>?,
        objects: Array<Any>? = null
    ): Fragment? {
        val f: Fragment?
        try {
            f = if (objects.isNullOrEmpty())
                clazz?.getDeclaredConstructor()?.newInstance()
            else
                clazz?.getDeclaredConstructor()?.newInstance(objects)
            return f
        } catch (e: Exception) {
            logE(TAG, e.cause.toString() + "\n" + e.stackTrace)
            return null
        }
    }
}