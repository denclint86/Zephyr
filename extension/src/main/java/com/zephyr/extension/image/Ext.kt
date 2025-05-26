package com.zephyr.extension.image

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

@SuppressLint("CheckResult")
fun ImageView.loadRadiusImage(
    imgUrl: String,
    radius: Int,
    enableCrossFade: Boolean = true,
    placeholder: Drawable? = null,
    error: Drawable? = null
) = set(imgUrl) {
    placeholder?.let { placeholder(it) }
    error?.let { error(it) }
    if (enableCrossFade)
        transition(DrawableTransitionOptions.withCrossFade())
    transform(CenterCrop(), RoundedCorners(radius))
}

@SuppressLint("CheckResult")
fun ImageView.loadCircleImage(
    imgUrl: String,
    enableCrossFade: Boolean = false,
    placeholder: Drawable? = null,
    error: Drawable? = null
) = set(imgUrl) {
    placeholder?.let { placeholder(it) }
    error?.let { error(it) }
    if (enableCrossFade)
        transition(DrawableTransitionOptions.withCrossFade())
    transform(CircleCrop())
}

/**
 * 没有任何偏好
 */
@SuppressLint("CheckResult")
fun ImageView.loadImage(
    imgUrl: String,
    enableCrossFade: Boolean = true,
    placeholder: Drawable? = null,
    error: Drawable? = null
) = set(imgUrl) {
    placeholder?.let { placeholder(it) }
    error?.let { error(it) }
    if (enableCrossFade)
        transition(DrawableTransitionOptions.withCrossFade())
    transform(CenterCrop())
}

private fun ImageView.set(
    imgUrl: String,
    preferences: RequestBuilder<Drawable>.() -> RequestBuilder<Drawable> = { this }
) {
    visibility = View.VISIBLE
    Glide.with(this)
        .load(imgUrl)
        .fitCenter()
        .preferences()
        .into(this)
}