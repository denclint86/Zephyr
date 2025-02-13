package com.zephyr.extension.thread

import android.os.Handler
import android.os.Looper

fun runOnMain(block: () -> Unit) = Handler(Looper.getMainLooper()).post(block)