package com.edipo2s.gistcomment

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.edipo2s.gistcomment.network.GlideCircleTransform

/**
 * Created by ediposouza on 01/11/16.
 */
fun Activity.showKeyboard() {
    currentFocus?.run {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, 0)
    }
}

fun Activity.hideKeyboard() {
    currentFocus?.run {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}

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