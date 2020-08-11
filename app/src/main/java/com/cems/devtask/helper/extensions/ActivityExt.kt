package com.cems.devtask.helper.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cems.devtask.R

fun Activity.hideKeyboard() {
    currentFocus?.let {
        it.clearFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun Activity.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun Activity.requestPermissionsSafely(permissions: Array<String>, requestCode: Int) {
    ActivityCompat.requestPermissions(this, permissions, requestCode)
}

fun Activity.startActivity(clazz: Class<*>, bundle: Bundle? = null) =
    startActivity(Intent(this, clazz).apply {
        bundle?.let { putExtras(it) }
    })

inline fun <reified T : Activity> Activity.startActivity(bundle: Bundle? = null) =
    startActivity(T::class.java, bundle)