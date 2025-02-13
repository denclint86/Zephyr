@file:Suppress("UNCHECKED_CAST")

package com.zephyr.vbclass

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.zephyr.log.logE
import java.lang.reflect.ParameterizedType

/**
 * 功能 : 利用 java 反射获取 viewbinding 的类
 *
 * 请注意泛型擦除所带来的影响
 *
 * 已经用具体的 viewbinding 实现的类不能再被继承,
 * 否则会由于找不到 viewbinding 而崩溃,
 * 具体原因可以看 getTypeList 函数
 */
interface ViewBindingInterface<VB : ViewDataBinding> {
    fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean = false
    ): VB {
        with(getTypeList()) {
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
    }

    fun getViewBinding(inflater: LayoutInflater): VB {
        with(getTypeList()) {
            val inflateMethod =
                this.getViewBindingClass().getDeclaredMethod(
                    "inflate",
                    LayoutInflater::class.java
                )
            val dataBinding = inflateMethod.invoke(null, inflater) as VB
            return dataBinding
        }
    }

    /**
     * 从 this.javaClass 中获取 vb 信息(即在最终的实现中),
     * 这意味着不能继承一个已经用 vb 实现过的类(除非再次在构造函数传入相同的 vb? 我没有尝试过),
     * 否则必然会因为找不到 vb 而崩溃
     */
    private fun getTypeList(): List<Class<VB>> {
        try {
            val parameterizedType = javaClass.genericSuperclass as ParameterizedType
            val arguments = parameterizedType.actualTypeArguments
            return arguments.filterIsInstance<Class<VB>>()
        } catch (e: Exception) {
            logE(
                this::class.simpleName.toString(),
                e.message.toString() + "\n" + e.stackTrace.toString()
            )
            return emptyList()
        }
    }

    /**
     * 从 vbClass 中找到目标类并返回
     */
    private fun List<Class<VB>>.getViewBindingClass(): Class<VB> {
        // 找到包含 "binding" 的索引
        val position =
            indexOfFirst { it.simpleName.endsWith("Binding") }

        if (position == -1) {
            val builder = StringBuilder("在这个 list 中找不到名称包含 'Binding' 的项:")
            forEach {
                builder.append("\n" + it.name)
            }
            throw IllegalStateException(builder.toString())
        }
        return get(position)
    }
}
