package com.zephyr.vbclass

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.ViewDataBinding
import com.zephyr.base.extension.TAG
import com.zephyr.base.log.logI

/**
 * @see ViewBindingInterface 注意事项
 */
abstract class ViewBindingActivity<VB : ViewDataBinding> : AppCompatActivity(),
    ViewBindingInterface<VB> {

    /**
     * 函数内可直接引用控件id
     */
    abstract fun VB.initBinding()

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!

    /**
     * 子类的 super 方法包含了 initBinding, 可以据此安排代码
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        logI(TAG, "$TAG onCreate", false)
        super.onCreate(savedInstanceState)
        _binding = getViewBinding(layoutInflater)
        setContentView(binding.root)
        binding.initBinding()
    }

    override fun onStart() {
        logI(TAG, "$TAG onStart", false)
        super.onStart()
    }

    override fun onResume() {
        logI(TAG, "$TAG onResume", false)
        super.onResume()
    }

    override fun onPause() {
        logI(TAG, "$TAG onPause", false)
        super.onPause()
    }

    override fun onStop() {
        logI(TAG, "$TAG onStop", false)
        super.onStop()
    }

    /**
     * 防止内存泄露
     */
    override fun onDestroy() {
        logI(TAG, "$TAG onDestroy", false)
        super.onDestroy()
        _binding?.unbind()
        _binding = null
    }

    protected fun enableFullScreen() {
        enableEdgeToEdge()

        val windowController = WindowCompat.getInsetsController(window, window.decorView)

        // 应用全屏时，用户仍然可以从屏幕顶部下拉唤出状态栏，此行代码实现当用户唤出状态栏后，自动隐藏状态栏
        windowController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // 隐藏包括状态栏、导航栏、caption bar 在内的所有系统栏
        windowController.hide(WindowInsetsCompat.Type.systemBars())

    }
}