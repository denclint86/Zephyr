package com.zephyr.log.models

import android.os.Handler
import android.os.Looper
import com.zephyr.log.interfaces.IExceptionHandler

/**
 * 异常处理模块, 可以设置监听器并捕捉来自所有线程的未被捕捉的异常, 在不设置的情况下, 记录日志并正常抛出
 */
internal class ExceptionHandler : IExceptionHandler {
    private var listener: ((Thread, Throwable) -> Boolean)? = null
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    val exceptionCallback: ((Thread, Throwable) -> Unit) = { thread, throwable ->
        val handled = listener?.invoke(thread, throwable) ?: false
        if (!handled)// 交给默认的 handler 接管
            defaultHandler?.uncaughtException(thread, throwable)
    }

    override fun register() {
        /**
         * 主线程捕获原理:
         * 用 handler 给主线程的 looper 添加新的任务——进入一个不断 loop 的死循环,
         * 此时原生的 loop 函数由于在内部嵌套了我们的循环,
         * 相当于被我们的 loop 接管了,
         * 而我们的 loop 被 try-catch 块包裹所以可以捕捉所有异常
         *
         * 关于 Lopper.loop()
         * 此函数在内部会获取所在线程的 looper 而不是使用 Lopper
         */
        Handler(Looper.getMainLooper()).post {
            while (true) {
                // 当捕捉并处理异常后不再阻塞原生的 loop,
                // 以便启动崩溃详情 activity
                try {
                    Looper.loop()
                } catch (t: Throwable) {
                    exceptionCallback(Looper.getMainLooper().thread, t)
                }
            }
        }

        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            exceptionCallback(t, e)
        }
    }

    override fun setOnCaughtListener(l: ((Thread, Throwable) -> Boolean)?) {
        listener = l
    }
}