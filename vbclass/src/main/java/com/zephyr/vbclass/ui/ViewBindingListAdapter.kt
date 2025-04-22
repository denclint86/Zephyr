package com.zephyr.vbclass.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zephyr.vbclass.ViewBindingInterface
import java.lang.reflect.Method

/**
 * @param VB databinding
 * @param D dataclass
 * @param itemCallback 自行实现 YourCallback: DiffUtil.ItemCallback<YourBean>()
 *
 * 由于 ViewHolder 为内部类, 需要尤其注意内存泄漏
 * @see ViewBindingInterface 注意事项
 */
abstract class ViewBindingListAdapter<VB : ViewDataBinding, D>(
    itemCallback: DiffUtil.ItemCallback<D>
) : ListAdapter<D, ViewBindingListAdapter<VB, D>.ViewHolder>(itemCallback),
    ViewBindingInterface<VB> {

    private val clazz: Class<VB> by lazy { getViewBindingClass() }
    private val inflateMethod: Method by lazy {
        clazz.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
    }

    /**
     * 可能内存泄漏, 但是从当前实现(为了传递泛型)来看似乎无法避免
     *
     * 无论是用抽象类间接传递还是像现在这样都是内部类. 也可能是我技术有限
     */
    inner class ViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            inflateMethod.invoke(null, LayoutInflater.from(parent.context), parent, false) as VB

        return ViewHolder(binding)
    }

    /**
     * 取代了 "onBindViewHolder"
     *
     * 不再需要写 "executePendingBindings()"
     */
    abstract fun VB.bind(data: D?, position: Int)

    /**
     * 不需要再重写
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            val item = try {
                getItem(position)
            } catch (_: Exception) {
                null
            }
            bind(item, position)
            executePendingBindings()
        }
    }
}