package com.cems.devtask.helper

import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.cems.devtask.GlideApp
import com.cems.devtask.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DataBindingAdapter {

    @JvmStatic
    @BindingAdapter("app:spiltDate")
    fun TextView.spiltDate(date: String?) {
        if (TextUtils.isEmpty(date)) return
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        val output = SimpleDateFormat("yyyy-MM-dd' 'h:mm a", Locale.ENGLISH)
        text = try {
            val parse = input.parse(date!!)
            output.format(parse ?: date)
        } catch (ignored: ParseException) {
            date
        }
    }

    @BindingAdapter("app:loadImage")
    @JvmStatic
    fun ImageView.loadImage(
        imageUrl: String?
    ) {
        GlideApp.with(context)
            .load(imageUrl?.replaceFirst("http://", "https://"))
            .thumbnail(0.1f)
            .circleCrop()
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(this)
    }


}