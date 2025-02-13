package com.zephyr.extension.activity

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import java.net.Inet4Address
import java.net.NetworkInterface

/**
 * 检查权限状态, 若未授予则先获取权限, 最后回调检查结果
 */
fun AppCompatActivity.withPermission(
    name: String = WRITE_EXTERNAL_STORAGE,
    callback: (isGranted: Boolean) -> Unit
) {
    if (isPermissionGranted(name)) {
        callback(true)
    } else {
        requestPermission(name, callback)
    }
}

/**
 * 尝试获取权限, 然后回调结果
 *
 * @param name Manifest.permission.XXX
 *
 * startActivityForResult 的简化版
 */
fun AppCompatActivity.requestPermission(
    name: String = WRITE_EXTERNAL_STORAGE,
    callback: (isGranted: Boolean) -> Unit
) = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
    callback(it)
}.launch(name)

/**
 * 是否给予了权限
 */
fun Activity.isPermissionGranted(name: String): Boolean = ContextCompat.checkSelfPermission(
    this,
    name
) == PackageManager.PERMISSION_GRANTED

fun Activity.showStatusBar() {
    WindowCompat.getInsetsController(window, window.decorView)
        .show(WindowInsetsCompat.Type.systemBars())
}

fun Activity.hideStatusBar() {
    WindowCompat.getInsetsController(window, window.decorView)
        .hide(WindowInsetsCompat.Type.systemBars())
}

/**
 * 根据 wifi / 流量获取 ip
 */
private fun Activity.getIpv4(): String? {
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
