package com.zephyr.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.net.Inet4Address
import java.net.NetworkInterface

private val prettyGson by lazy {
    GsonBuilder().setPrettyPrinting().create()
}

private val gson by lazy { Gson() }

fun Any?.toPrettyJson(): String {
    val result = if (this is CharSequence) {
        prettyGson.toJson(this.toJsonElement())
    } else
        prettyGson.toJson(this)
    return if (result == "null") "" else result
}

fun Any?.toJson(): String {
    val result = gson.toJson(this)
    return if (result == "null") "" else result
}

fun CharSequence.toJsonElement(): JsonElement? = try {
    JsonParser.parseString(this.toString())
} catch (_: Throwable) {
    null
}

inline fun <reified T> String.toJsonClass(): T? {
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

fun Context.getIpv4(): String? {
    var ip: String? = null

    try {
        val wifiManager: WifiManager =
            getSystemService(Context.WIFI_SERVICE) as WifiManager
        val connectivityManager: ConnectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (wifiManager.isWifiEnabled) {
            val network = connectivityManager.activeNetwork ?: return ip
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return ip
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                val wifiInfo = wifiManager.connectionInfo
                val ipAddress = wifiInfo.ipAddress

                ip = (ipAddress and 0xFF).toString() + "." +
                        ((ipAddress shr 8) and 0xFF) + "." +
                        ((ipAddress shr 16) and 0xFF) + "." +
                        (ipAddress shr 24 and 0xFF)
            }
        } else {
            NetworkInterface.getNetworkInterfaces().toList().forEach { networkInterface ->
                networkInterface.inetAddresses.toList().forEach { address ->
                    address.run {
                        if (!isLoopbackAddress && this is Inet4Address) {
                            if (!hostAddress.isNullOrBlank())
                                ip = hostAddress!!
                        }
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ip
}
