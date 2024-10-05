@file:Suppress("UNCHECKED_CAST")

package com.p1ay1s.dev.viewbinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import java.lang.reflect.ParameterizedType

/**
 * 功能 : 利用 java 反射获取 view binding 的类
 *
 * 由于需要获取 java class 所以封装成接口比较合适
 * 将实现打包成 jar 可能无法获取到 ViewBinding 类
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
    ): VB {
        try {
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
        } catch (e: Exception) {
            val exceptionType = e.javaClass.simpleName
            val exceptionMessage = e.message
            val exceptionStackTrace = e.stackTrace.toString()
            throw IllegalArgumentException(
                "can not get ViewBinding instance through reflection!" +
                        "\nException type: $exceptionType" +
                        "\nMessage: $exceptionMessage" +
                        "\nStacktrace:" +
                        "\n$exceptionStackTrace"
            )
        }
    }

    fun getViewBinding(inflater: LayoutInflater): VB = try {
        with((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()) {
            val inflateMethod =
                this.getViewBindingClass().getDeclaredMethod(
                    "inflate",
                    LayoutInflater::class.java
                )
            val dataBinding = inflateMethod.invoke(null, inflater) as VB
            return dataBinding
        }
    } catch (e: Exception) {
        val exceptionType = e.javaClass.simpleName
        val exceptionMessage = e.message
        val exceptionStackTrace = e.stackTrace.toString()
        throw IllegalArgumentException(
            "can not get ViewBinding instance through reflection!" +
                    "\nException type: $exceptionType" +
                    "\nMessage: $exceptionMessage" +
                    "\nStacktrace:" +
                    "\n$exceptionStackTrace"
        )
    }
}