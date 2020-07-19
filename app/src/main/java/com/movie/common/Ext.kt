package com.movie.common

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.movie.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun ImageView.setImage(imgUri: String) {
    Glide.with(context)
        .load(imgUri)
        .error(R.drawable.ic_no_movie)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

fun ImageView.setImage(imgUri: Int) {
    Glide.with(context)
        .load(imgUri)
        .error(R.drawable.ic_no_movie)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

fun ImageView.setGray(icon: Int) {
    val ic = ContextCompat.getDrawable(context, icon)
    ic?.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)
    this.setImageDrawable(ic)
}

fun ImageView.setRed(icon: Int) {
    val ic = ContextCompat.getDrawable(context, icon)
    ic?.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP)
    this.setImageDrawable(ic)
}

@SuppressLint("SimpleDateFormat")
fun String.formatDate(): String {
    val input = SimpleDateFormat("yyyy-MM-dd")
    val output = SimpleDateFormat("MMM dd, yyyy")
    var d: Date? = null
    try {
        d = input.parse(this)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return output.format(d!!)?: ""
}