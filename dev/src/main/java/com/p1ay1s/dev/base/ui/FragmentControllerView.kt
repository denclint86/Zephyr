package com.p1ay1s.dev.base.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.p1ay1s.dev.base.getKey
import com.p1ay1s.dev.base.log.getFunctionName
import com.p1ay1s.dev.base.removeByValue

/**
 * 具有 fragment 管理能力的 view
 */
class FragmentControllerView : FrameLayout {
    lateinit var fragmentManager: FragmentManager
    protected lateinit var fragmentMap: LinkedHashMap<String, Fragment> // linkedHashMap 可以按 item 添加顺序排列
    protected lateinit var currentIndex: String

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun submitMap(map: LinkedHashMap<String, Fragment>) {
        fragmentMap = map
    }

    protected fun isThisInitialized(): Boolean {
        return ::fragmentManager.isInitialized && ::fragmentMap.isInitialized && ::currentIndex.isInitialized
    }

    fun init() {
        if (isThisInitialized()) return
        currentIndex = fragmentMap.keys.first()
        fragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            fragmentMap.forEach { (index, fragment) ->
                add(id, fragment, index)
                hide(fragment)
            }

            fragmentMap[currentIndex]?.let { show(it) }
        }.commit()
    }

    protected fun getCurrentFragment() = getFragment(currentIndex)

    protected fun getFragment(index: String?): Fragment {
        if (!isThisInitialized()) throw IllegalStateException("${getFunctionName()}has not be initialized")
        val fragment = fragmentMap[index]
            ?: throw IllegalStateException("${getFunctionName()}cannot find fragment with index $index")
        return fragment
    }

    fun switchToFragment(target: Fragment) {
        if (!isThisInitialized()) throw IllegalStateException("${getFunctionName()}has not be initialized")
        fragmentMap.values.forEach {
            if (it == target) {
                if (target == getCurrentFragment()) return
                fragmentManager.beginTransaction().apply {
                    hide(getCurrentFragment())
                    show(target)
                }.commitNow()

                val key = fragmentMap.getKey(target)
                    ?: throw IllegalStateException("${getFunctionName()}key cannot be null")
                currentIndex = key
                return
            }
        }
        throw IllegalStateException("${getFunctionName()}cannot find fragment")
    }

    fun switchToFragment(target: String) =
        fragmentMap[target]?.let { switchToFragment(it) }

    fun addFragment(index: String, fragment: Fragment, show: Boolean = true) {
        if (!isThisInitialized()) throw IllegalStateException("${getFunctionName()}has not be initialized")
        fragmentMap.keys.forEach {
            if (it == index) {
                return
            }
        }
        fragmentManager.beginTransaction().apply {
            fragmentMap[index] = fragment
            add(id, fragment, index)
        }.commitNow()

        if (show) switchToFragment(index)
    }

    fun deleteFragment(target: Fragment) {
        if (!isThisInitialized()) throw IllegalStateException("${getFunctionName()}has not be initialized")
        fragmentManager.beginTransaction().apply {
            target.let {
                hide(it)
                detach(it)
            }
        }.commitNow()

        fragmentMap.removeByValue(target)
    }

    fun deleteFragment(target: String) =
        deleteFragment(getFragment(target))
}
