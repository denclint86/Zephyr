package com.p1ay1s.dev.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.p1ay1s.dev.base.getKey
import com.p1ay1s.dev.base.removeByValue
import com.p1ay1s.dev.base.throwException

/**
 * 具有 fragment 管理能力的 view
 */
open class FragmentControllerView : FrameLayout {
    lateinit var fragmentManager: FragmentManager
    lateinit var fragmentMap: LinkedHashMap<String, Fragment> // linkedHashMap 可以按 item 添加顺序排列
    protected lateinit var currentIndex: String

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

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
        checkIfIsInitialized()

        with(fragmentMap[index]) {
            if (this == null) throwException("cannot find fragment with index $index")
            return this!!
        }
    }

    fun switchToFragment(target: Fragment) {
        checkIfIsInitialized()

        fragmentMap.values.forEach {
            if (it == target) {
                if (target == getCurrentFragment()) return
                fragmentManager.beginTransaction().apply {
                    hide(getCurrentFragment())
                    show(target)
                }.commitNow()

                with(fragmentMap.getKey(target)) {
                    if (this == null) throwException("key cannot be null")
                    currentIndex = this!!
                    return
                }
            }
        }
        throwException("cannot find fragment")
    }

    fun switchToFragment(target: String) =
        fragmentMap[target]?.let { switchToFragment(it) }

    fun addFragment(index: String, fragment: Fragment, show: Boolean = true) {
        checkIfIsInitialized()

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
        checkIfIsInitialized()

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

    private fun checkIfIsInitialized() {
        if (!isThisInitialized()) throwException("has not be initialized")
    }
}
