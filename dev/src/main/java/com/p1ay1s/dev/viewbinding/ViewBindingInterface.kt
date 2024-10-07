@file:Suppress("UNCHECKED_CAST")

package com.p1ay1s.dev.viewbinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import java.lang.reflect.ParameterizedType

/**
 * 功能 : 利用 java 反射获取 viewbinding 的类
 *
 * 已经用具体的 viewbinding 实现的类不能再被继承,
 * 否则会由于找不到 viewbinding 而崩溃
 *
 * 取消了 try-catch , 有时候把异常捕捉起来反而误事
 */
interface ViewBindingInterface<VB : ViewDataBinding> {
    /**
     * 从 vbClass 中找到目标类并返回
     */
    private fun List<Class<VB>>.getViewBindingClass(): Class<VB> {
        // 找到包含 "binding" 的索引
        val position =
            indexOfFirst { it.simpleName.endsWith("Binding") }

        if (position == -1) {
            val builder = StringBuilder("cannot find a class named 'Binding' in this list:")
            forEach {
                builder.append("\n" + it.name)
            }
            throw IllegalStateException(builder.toString())
        }
        return get(position)
    }

    fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean = false
    ): VB =
        with((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()) {
            val inflateMethod = this.getViewBindingClass().getDeclaredMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.java
            )
            val dataBinding =
                inflateMethod.invoke(null, inflater, container, attachToRoot) as VB
            return dataBinding
        }

    fun getViewBinding(inflater: LayoutInflater): VB =
        with((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()) {
            val inflateMethod =
                this.getViewBindingClass().getDeclaredMethod(
                    "inflate",
                    LayoutInflater::class.java
                )
            val dataBinding = inflateMethod.invoke(null, inflater) as VB
            return dataBinding
        }
}
