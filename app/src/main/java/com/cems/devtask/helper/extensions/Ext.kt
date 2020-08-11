package com.cems.devtask.helper.extensions

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cems.devtask.BuildConfig
import com.cems.devtask.R
import com.cems.devtask.helper.LocaleManager
import com.google.gson.Gson
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Context.isArabic() = LocaleManager.getLanguage(this) == LocaleManager.arLanguage

@Suppress("DEPRECATION")
fun Context.isNetworkConnected(): Boolean {
    var result = false
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager?.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager?.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI,
                    ConnectivityManager.TYPE_MOBILE,
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }

            }
        }
    }

    return result
}

fun EditText.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun String.isValidPhone(): Boolean {
    return Patterns.PHONE.matcher(this).matches() && length in 6..11
}

fun String.isValidEmailAddress(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun View.loadAnimation(@AnimRes animRes: Int) {
    visibility = View.VISIBLE
    val animation = AnimationUtils.loadAnimation(context, animRes)
    startAnimation(animation)
}

fun SharedPreferences.Editor.putObject(key: String, myObject: Any?): SharedPreferences.Editor {
    return if (myObject == null)
        remove(key)
    else
        putString(key, Gson().toJson(myObject))
}

fun <H> SharedPreferences.getObject(key: String, className: Class<H>?): H? {
    val json = getString(key, "")
    return if (TextUtils.isEmpty(json)) null else Gson().fromJson(json, className)
}

fun log(tag: String = "mTag", message: String?, exception: Throwable? = null) {
    if (BuildConfig.DEBUG)
        Log.wtf(tag, message, exception)
}

//fun getRandomString(context: Context) = context.getString(R.string.terms).substring(0, ((20..200).random()))

fun View.setShowing(show: Boolean) {
    if (show && visibility == View.VISIBLE) return
    if (!show && visibility == View.GONE) return
    visibility = View.VISIBLE
    val animation = AnimationUtils.loadAnimation(
        context,
        if (show) R.anim.nav_default_enter_anim else R.anim.nav_default_exit_anim
    )
    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
            visibility = if (show) View.VISIBLE else View.GONE
        }

        override fun onAnimationRepeat(animation: Animation?) {

        }
    })
    startAnimation(animation)
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, onChanged: (T) -> Unit) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T) {
            onChanged.invoke(t)
            removeObserver(this)
        }
    })
}

fun Long.toDateTime(pattern: String = "yyyy-MM-dd", locale: Locale = Locale.ENGLISH): String {
    val date = Date(this)
    val format = SimpleDateFormat(pattern, locale)
    return format.format(date)
}

fun String.fromDateTime(pattern: String = "yyyy-MM-dd", locale: Locale = Locale.ENGLISH): Date {
    val format = SimpleDateFormat(pattern, locale)
    return try {
        format.parse(this) ?: Date()
    } catch (ex: ParseException) {
        Date()
    }
}

fun String?.toInt(default: Int = 0): Int {
    if (this.isNullOrEmpty()) return default
    return try {
        Integer.parseInt(this)
    } catch (ex: NumberFormatException) {
        default
    }
}

fun <T> MutableLiveData<T>.notifyObserver() {
    value = this.value
}

fun Menu.setMenuItemsVisibility(exception: MenuItem, visible: Boolean) {
    for (i in 0 until size()) {
        val item: MenuItem = getItem(i)
        if (item.itemId != exception.itemId) item.isVisible = visible
    }
}

fun TextView.setMultiTextColor(vararg texts: Pair<String, Int>) {
    text = null
    for (text in texts) {
        append(SpannableString(text.first).apply {
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, text.second)),
                0, this.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        })
    }
}

fun TextView.setMultiTextColorResources(vararg texts: Pair<Int, Int>) {
    text = null
    for (text in texts) {
        append(SpannableString(context.getString(text.first)).apply {
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, text.second)),
                0, this.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        })
    }
}

fun postDelayed(time: Long, runnable: () -> Unit) =
    Handler(Looper.getMainLooper()).postDelayed(runnable, time)

fun View.toTransitionGroup() = this to transitionName

fun RecyclerView.scrollToCenterPosition(position: Int) {
    val firstPosition = (layoutManager as?
            LinearLayoutManager)?.findFirstVisibleItemPosition() ?: 0
    val lastPosition = (layoutManager as?
            LinearLayoutManager)?.findLastVisibleItemPosition() ?: (adapter?.itemCount ?: 1) - 1
    val centerPosition = (firstPosition + lastPosition) / 2
    when {
        position > centerPosition -> scrollToPosition(position - 1)
        position < centerPosition -> scrollToPosition(position + 1)
        else -> scrollToPosition(position)
    }
}
