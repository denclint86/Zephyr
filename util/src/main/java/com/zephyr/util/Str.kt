package com.zephyr.util

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.GsonBuilder

fun Any?.toPrettyJson(): String {
    val gson = GsonBuilder().setPrettyPrinting().create()
    val result = gson.toJson(this)
    return if (result == "null") "" else result
}

fun Any?.toJson(): String {
    val result = Gson().toJson(this)
    return if (result == "null") "" else result
}

inline fun <reified T> String?.toJsonClass(): T? {
    return try {
        Gson().fromJson(this, T::class.java)
    } catch (_: Exception) {
        null
    }
}

fun toBase64String(str: String): String = Base64.encodeToString(
    str.toByteArray(),
    Base64.NO_WRAP
)