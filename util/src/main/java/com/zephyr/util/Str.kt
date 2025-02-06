package com.zephyr.util

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser

fun Any?.toPrettyJson(): String {
    val gson = GsonBuilder().setPrettyPrinting().create()
    val result = if (this is CharSequence) {
        gson.toJson(this.toJsonElement())
    } else
        gson.toJson(this)
    return if (result == "null") "" else result
}

fun Any?.toJson(): String {
    val result = Gson().toJson(this)
    return if (result == "null") "" else result
}

fun CharSequence.toJsonElement(): JsonElement? = try {
    JsonParser.parseString(this.toString())
} catch (_: Throwable) {
    null
}

inline fun <reified T> String?.toJsonClass(): T? {
    return try {
        Gson().fromJson(this, T::class.java)
    } catch (_: Exception) {
        null
    }
}

fun String.toBase64String(): String = Base64.encodeToString(
    this.toByteArray(),
    Base64.NO_WRAP
)