package com.p1ay1s.dev.base.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.p1ay1s.dev.base.log.getFunctionName
import com.p1ay1s.dev.base.log.logE

/**
 * 具有 fragment 管理能力的 view
 */
class FragmentControllerView : FrameLayout {
    private val TAG = this::class.simpleName!!
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentMap: LinkedHashMap<String, Fragment> // linkedHashMap 可以按 item 添加顺序排列
    private lateinit var currentIndex: String

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setFragmentManager(fragmentManager: FragmentManager) {
        this.fragmentManager = fragmentManager
    }

    fun submitMap(map: LinkedHashMap<String, Fragment>) {
        fragmentMap = map
    }

    private fun isInitialized(): Boolean {
        return ::fragmentManager.isInitialized && ::fragmentMap.isInitialized && ::currentIndex.isInitialized
    }

    fun init() {
        if (isInitialized()) return
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

    fun getCurrentFragment() = getFragment(currentIndex)

    private fun getFragment(index: String?): Fragment {
        if (!isInitialized()) throw IllegalStateException("${getFunctionName()}has not be initialized")
        val fragment = fragmentMap[index]
            ?: throw IllegalStateException("${getFunctionName()}cannot find fragment with index $index")
        return fragment
    }

    /**
     * 切换至目标索引的 fragment
     */
    fun switchToFragment(index: String) {
        if (!isInitialized()) throw IllegalStateException("${getFunctionName()}has not be initialized")
        fragmentMap.keys.forEach {
            if (it == index) {
                if (index == currentIndex) return
                fragmentManager.beginTransaction().apply {
                    hide(getCurrentFragment())
                    show(getFragment(index))
                }.commitNow()

                currentIndex = index
                return
            }
        }
        throw IllegalStateException("${getFunctionName()}cannot find fragment with index $index")
    }

    /**
     * 添加并显示 fragment
     */
    fun addAndSwitchToFragment(index: String, fragment: Fragment) {
        if (!isInitialized()) throw IllegalStateException("${getFunctionName()}has not be initialized")
        fragmentMap.keys.forEach {
            if (it == index) {
                logE(TAG, "index $index is already added!")
                return
            }
        }
        fragmentManager.beginTransaction().apply {
            fragmentMap[index] = fragment
            add(id, fragment, index)
            switchToFragment(index)
        }.commitNow()
    }

    /**
     * 移除 fragment
     */
    fun deleteFragment(deleteIndex: String) {
        if (!isInitialized()) throw IllegalStateException("${getFunctionName()}has not be initialized")
        fragmentManager.beginTransaction().apply {
            getFragment(deleteIndex).let {
                fragmentMap.remove(deleteIndex)
                hide(it)
                detach(it)
            }
        }.commitNow()
    }
}