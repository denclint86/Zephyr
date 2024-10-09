package com.p1ay1s.dev.viewbinding.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.p1ay1s.dev.viewbinding.ViewBindingInterface

/**
 * @param VB databinding
 * @param D dataclass
 * @param C content 后面可能需要的内容
 * @param itemCallback 自行实现 YourCallback: DiffUtil.ItemCallback<YourBean>()
 *
 * @see ViewBindingInterface 注意事项
 */
abstract class ViewBindingListAdapter<VB : ViewDataBinding, D, C>(
    itemCallback: DiffUtil.ItemCallback<D>
) : ListAdapter<D, ViewBindingListAdapter<VB, D, C>.ViewHolder>(itemCallback),
    ViewBindingInterface<VB> {

    protected lateinit var mBinding: VB

    inner class ViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root) {
        init {
            mBinding = binding
        }
    }

//    protected val collector = object : ContentCollector<D, C?>(3) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return with(getViewBinding(LayoutInflater.from(parent.context), parent)) {
            ViewHolder(this)
        }
    }

    /**
     * 取代了 "onBindViewHolder"
     *
     * 不再需要写 "executePendingBindings()"
     */
    abstract fun VB.onBindViewHolder(data: D, position: Int)

    /**
     * 不需要再重写
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            onBindViewHolder(getItem(position), position)
            executePendingBindings()
        }
    }

//
//    abstract class ContentCollector<KEY, VALUE>(val max: Int = 9) {
//        /**
//         * 缓存若干内容, 并且自动删除最早的内容
//         */
//        private val cache = object : LinkedHashMap<KEY, VALUE>(max, 0.75f, true) {
//            override fun removeEldestEntry(eldest: Map.Entry<KEY, VALUE>): Boolean {
//                return size > max
//            }
//        }
//
//        fun get(key: KEY): VALUE? {
//            return cache[key]
//        }
//
//        fun put(key: KEY, value: VALUE) {
//            cache[key] = value
//        }
//    }
}