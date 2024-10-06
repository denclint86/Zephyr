@file:Suppress("NOTHING_TO_INLINE")

package com.p1ay1s.dev.base

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// 主要是一些拓展的函数

var appContext: Context? = null
var appBaseUrl = ""
var appIpAddress = ""

val Activity.TAG
    get() = this::class.simpleName!!
val Fragment.TAG
    get() = this::class.simpleName!!
val ViewModel.TAG
    get() = this::class.simpleName!!

/**
 * @param name Manifest.permission.XXX
 */
fun AppCompatActivity.requestPermission(
    name: String = WRITE_EXTERNAL_STORAGE,
    callback: (isGranted: Boolean) -> Unit
) = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
    callback(it)
}.launch(name)

fun Activity.isPermissionGranted(name: String): Boolean = ContextCompat.checkSelfPermission(
    this,
    name
) == PackageManager.PERMISSION_GRANTED

suspend fun toastSuspended(msg: String, length: Int = Toast.LENGTH_SHORT) =
    withContext(Dispatchers.Main) {
        toast(msg, length)
    }

fun toast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    if (msg.isNotBlank())
        Toast.makeText(appContext, msg, length).show()
}

fun Any?.toast() {
    val str = this.toString()
    if (str.isNotBlank()) toast(str)
}

inline fun getFunctionName(): String {
    val functionName = object {}.javaClass.enclosingMethod?.name
    return if (functionName != "getFunctionName") {
        "$functionName: "
    } else {
        ""
    }
}

fun <K, V> Map<K, V>.getKey(target: V): K? {
    for ((key, value) in this)
        if (target == value)
            return key
    return null
}

fun <K, V> HashMap<K, V>.removeByValue(value: V) =
    getKey(value)?.let { remove(it) }

fun throwException(msg: String) {
    throw IllegalStateException(getFunctionName() + msg)
}