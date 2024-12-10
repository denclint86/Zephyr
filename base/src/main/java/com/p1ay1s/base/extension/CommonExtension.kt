@file:Suppress("NOTHING_TO_INLINE")

package com.p1ay1s.base.extension

import androidx.lifecycle.ViewModel

inline fun getFunctionName(): String {
    val functionName = object {}.javaClass.enclosingMethod?.name
    return if (functionName != "getFunctionName") {
        "$functionName: "
    } else {
        ""
    }
}

val ViewModel.TAG
    get() = this::class.simpleName!!

fun <K, V> Map<K, V>.getKey(target: V): K? {
    for ((key, value) in this)
        if (target == value)
            return key
    return null
}

fun <K, V> HashMap<K, V>.removeByValue(value: V) =
    getKey(value)?.let { remove(it) }
