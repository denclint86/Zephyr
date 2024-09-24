package com.p1ay1s.extensions

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.p1ay1s.dev.base.appContext
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