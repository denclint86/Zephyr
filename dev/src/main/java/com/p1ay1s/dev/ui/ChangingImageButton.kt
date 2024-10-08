package com.p1ay1s.dev.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 可以自动切换图片的 view,
 * 点击事件可以选择 suspend 或者直接在主线程执行,
 * 有波纹点击效果
 *
 * 限制宽高为其中的最小值以使得 view 是正方形
 */
open class ChangingImageButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr) {
    enum class Status {
        IMAGE_A, IMAGE_B
    }

    var rippleColor = "#40000000"
    protected var mListener: (suspend () -> Status)? = null
    protected val viewScope: CoroutineScope by lazy { CoroutineScope(Dispatchers.IO) }
    protected var job: Job? = null
    protected var imageA: Int? = null
    protected var imageB: Int? = null

    init {
        scaleType = ScaleType.FIT_CENTER

        background = RippleDrawable(
            ColorStateList.valueOf(Color.parseColor(rippleColor)), null, ShapeDrawable(OvalShape())
        )
    }

    /**
     * 两个监听器的设置会相互覆盖
     */
    fun setOnSuspendClickListener(listener: suspend () -> Status) {
        mListener = listener
        super.setOnClickListener {
            handleClick()
        }
    }

    fun setOnClickListener(listener: () -> Status) {
        super.setOnClickListener {
            val result = listener()
            switchImage(result)
        }
    }

    fun setImageResources(imageA: Int, imageB: Int) {
        this.imageA = imageA
        this.imageB = imageB
        switchImage(Status.IMAGE_A)
    }

    private fun switchImage(status: Status) {
        val id = if (status == Status.IMAGE_A) imageA else imageB
        id ?: return
        setImageResource(id)
    }

    protected fun handleClick() {
        if (mListener == null) return
        viewScope.launch {
            job?.cancel()
            job?.join()
            job = launch(Dispatchers.IO) {
                val result = mListener!!()
                withContext(Dispatchers.Main) {
                    switchImage(result)
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        cutView()
        super.onDraw(canvas)
    }

    /**
     * 设置等宽高
     */
    private fun cutView() {
        layoutParams.run {
            width = minOf(width, height)
            height = width
            requestLayout()
        }
    }
}