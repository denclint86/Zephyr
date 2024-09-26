package com.p1ay1s.dev.util

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

object ImageSetter {
    private const val RADIUS = 25

    private fun ImageView.setVisible() {
        visibility = View.VISIBLE
    }

    fun ImageView.setRadiusImgView(
        imgUrl: String,
        radius: Int = RADIUS,
        enableCrossFade: Boolean = true,
        enableCache: Boolean = false
    ) = set(imgUrl, enableCrossFade, enableCache) {
        if (enableCrossFade)
            transition(DrawableTransitionOptions.withCrossFade())
        transform(CenterCrop(), RoundedCorners(radius))
    }

    fun ImageView.setCircleImgView(
        imgUrl: String,
        enableCrossFade: Boolean = false,
        enableCache: Boolean = false
    ) = set(imgUrl, enableCrossFade, enableCache) {
        if (enableCrossFade)
            transition(DrawableTransitionOptions.withCrossFade())
        transform(CircleCrop())
    }

    /**
     * 没有任何偏好
     */
    fun ImageView.setImgView(
        imgUrl: String,
        enableCrossFade: Boolean = true,
        enableCache: Boolean = false
    ) = set(imgUrl, enableCrossFade, enableCache) {
        if (enableCrossFade)
            transition(DrawableTransitionOptions.withCrossFade())
        transform(CenterCrop())
    }

    fun ImageView.set(
        imgUrl: String,
        enableCrossFade: Boolean,
        enableCache: Boolean,
        preferences: RequestBuilder<Drawable>.() -> RequestBuilder<Drawable> = { this }
    ) {
        setVisible()
        Glide.with(this)
            .load(imgUrl)
            .fitCenter()
            .skipMemoryCache(!enableCache)
            .preferences()
            .into(this)
    }
}
