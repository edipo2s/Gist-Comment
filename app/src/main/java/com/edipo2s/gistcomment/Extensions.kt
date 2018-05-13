package com.edipo2s.gistcomment

import android.graphics.drawable.Drawable
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.edipo2s.gistcomment.network.GlideCircleTransform

/**
 * Created by ediposouza on 01/11/16.
 */
fun ViewGroup.inflate(@LayoutRes resource: Int): View {
    return LayoutInflater.from(context).inflate(resource, this, false)
}

fun ImageView.loadFromUrl(imageUrl: String, placeholder: Drawable? = null,
                          circleTransformation: Boolean = false) {
    val requestOptions = RequestOptions().placeholder(placeholder)
    if (circleTransformation) {
        requestOptions.transform(GlideCircleTransform())
    }
    Glide.with(context)
            .load(imageUrl)
            .apply(requestOptions)
            .transition(DrawableTransitionOptions().crossFade(500))
            .into(this)
}