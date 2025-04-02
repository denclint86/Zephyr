package com.zephyr.extension.thread

import android.os.Handler
import android.os.Looper

private val mainLooper: Looper by lazy { Looper.getMainLooper() }
fun runOnMain(block: () -> Unit) = Handler(mainLooper).post(block)