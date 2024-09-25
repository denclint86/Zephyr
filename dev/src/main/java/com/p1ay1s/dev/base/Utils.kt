@file:Suppress("NOTHING_TO_INLINE")

package com.p1ay1s.dev.base

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val Activity.TAG
    get() = this::class.simpleName!!
val Fragment.TAG
    get() = this::class.simpleName!!
val ViewModel.TAG
    get() = this::class.simpleName!!

suspend fun toastSuspended(msg: String, length: Int = Toast.LENGTH_SHORT) =
    withContext(Dispatchers.Main) {
        toast(msg, length)
    }

fun toast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    if (msg.isNotBlank())
        Toast.makeText(appContext, msg, length).show()
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