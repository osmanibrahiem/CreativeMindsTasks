package com.cems.devtask.helper.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment

fun Fragment.hideKeyboard() {
    activity?.currentFocus?.let {
        it.clearFocus()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun Fragment.getSupportActionBar(): ActionBar? = (activity as? AppCompatActivity)?.supportActionBar

fun Fragment.setSupportActionBar(toolbar: Toolbar) {
    (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
}

fun Fragment.setBackActionBar(
    toolbar: Toolbar,
    @DrawableRes icon: Int = 0,
    listener: (View) -> Unit = { requireActivity().onBackPressed() }
) {
    setSupportActionBar(toolbar)
    if (icon != 0)
        getSupportActionBar()?.setHomeAsUpIndicator(icon)
    getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
    getSupportActionBar()?.setDisplayShowHomeEnabled(true)
    toolbar.setNavigationOnClickListener(listener)
}

fun Fragment.openUrl(url: String?) {
    if (url == null) return
    var uri: Uri = url.toUri()
    if (!url.startsWith("http://") && !url.startsWith("https://"))
        uri = "https://$url".toUri()
    startActivity(Intent(Intent.ACTION_VIEW, uri))
}

fun Fragment.startActivity(clazz: Class<*>, bundle: Bundle? = null) =
    startActivity(Intent(context, clazz).apply {
        bundle?.let { putExtras(it) }
    })

inline fun <reified T : Activity> Fragment.startActivity(bundle: Bundle? = null) =
    startActivity(T::class.java, bundle)

fun Fragment.waitForTransition(targetView: View) {
    postponeEnterTransition()
    targetView.doOnPreDraw { startPostponedEnterTransition() }
}

