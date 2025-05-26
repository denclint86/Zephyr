package com.zephyr.tools

import android.content.Context
import android.widget.Toast
import com.zephyr.provider.Zephyr

@JvmName("toast1")
fun Any?.toast(
    cancelLast: Boolean = true,
    length: Int = Toast.LENGTH_SHORT
) = Toaster().toast(this, Zephyr.application, cancelLast, length)

@JvmName("toast2")
fun toast(
    msg: Any?,
    cancelLast: Boolean = true,
    length: Int = Toast.LENGTH_SHORT
) = Toaster().toast(msg, Zephyr.application, cancelLast, length)

class Toaster {

    fun toast(
        msg: Any?,
        context: Context = Zephyr.application,
        cancelLast: Boolean = true,
        length: Int = Toast.LENGTH_SHORT
    ) = runCatching {
        if (cancelLast) {
            Toast(context).cancel()
        }
        Toast.makeText(context, msg?.toString() ?: "null object", length).show()
    }
}