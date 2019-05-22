package com.dudo.sleepsafe.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.core.app.ActivityCompat


inline fun <reified T : ViewModel> androidx.fragment.app.FragmentActivity.injectViewModel(factory: ViewModelProvider.Factory): T {
    return ViewModelProviders.of(this, factory)[T::class.java]
}

inline fun <reified T : ViewModel> androidx.fragment.app.Fragment.injectViewModel(factory: ViewModelProvider.Factory): T {
    return ViewModelProviders.of(this, factory)[T::class.java]
}

fun Context.dpToPx(dp: Float) = dp * this.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT

fun Fragment.checkCameraPermission(): Boolean =
    context?.let {
        ActivityCompat.checkSelfPermission(it, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    } ?: false

fun Fragment.toast(msg: String) {
    Toast.makeText(this.context, msg, Toast.LENGTH_LONG).show()
}

fun Fragment.toast(id: Int) {
    Toast.makeText(this.context, id, Toast.LENGTH_LONG).show()
}