@file:Suppress("UNCHECKED_CAST")

package com.p1ay1s.dev.base.vb

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import java.lang.reflect.ParameterizedType

/**
 * 功能 : 利用 java 反射获取 view binding 的类
 */
interface ViewBindingInterface<VB : ViewDataBinding> {

    /**
     * 查找类名中有"binding"的索引, 未找到时抛出异常
     */
    fun getViewBindingPosition(vbClass: List<Class<VB>>): Int {
        // 找到包含 "binding" 的索引
        val position = vbClass.indexOfFirst { it.name.contains("binding", ignoreCase = true) }

        if (position == -1)
            throw IllegalStateException("cannot find class whose name contains \"binding\"")
        return position
    }

    fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): VB = try {
        with((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()) {
            val inflateMethod = this[getViewBindingPosition(this)].getDeclaredMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.java
            )
            val dataBinding = inflateMethod.invoke(null, inflater, container, false) as VB
            return dataBinding
        }
    } catch (e: Exception) {
        e.printStackTrace()
        throw IllegalArgumentException("can not get ViewBinding instance through reflection!")
    }

    fun getViewBinding(inflater: LayoutInflater): VB = try {
        with((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()) {
            val inflateMethod =
                this[getViewBindingPosition(this)].getDeclaredMethod("inflate", LayoutInflater::class.java)
            val dataBinding = inflateMethod.invoke(null, inflater) as VB
            return dataBinding
        }
    } catch (e: Exception) {
        e.printStackTrace()
        throw IllegalArgumentException("can not get ViewBinding instance through reflection!")
    }
}