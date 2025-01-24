package com.zephyr.app

import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.zephyr.app.databinding.ActivityMainBinding
import com.zephyr.base.extension.toast
import com.zephyr.base.extension.withPermission
import com.zephyr.vbclass.ViewBindingActivity

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainActivity : ViewBindingActivity<ActivityMainBinding>() {

    private lateinit var vibrator: Vibrator

    override fun ActivityMainBinding.initBinding() {
        enableEdgeToEdge()

        withPermission(android.Manifest.permission.POST_NOTIFICATIONS) {
            (if (it) "已授权" else "未授权").toast()
        }

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        nav.setOnItemSelectedListener { item ->
            vibrator.vibrate(50L)
            hostView.switchHost(item.itemId, R.anim.fade_in, R.anim.fade_out)
            true
        }

        with(hostView) {
            fragmentManager = supportFragmentManager
//            setOnHostChangeListener { _, _ -> }
            addHost(R.id.index_3) // 三种不同的压入方式
            hostView.getActiveHost()?.pushFragment(_3a, Fragment3a())

            addHost(R.id.index_2, _2a, Fragment2a())
            addHost(R.id.index_1, _1a, Fragment1a::class.java)
        }

//        nav.setOnItemReselectedListener {  }
    }

    override fun onStart() {
        super.onStart()
        binding.hostView.getActiveHost()?.show() // 防止残留
    }

    override fun onStop() {
        binding.hostView.getActiveHost()?.hide() // 防止残留
        super.onStop() // on stop 之后不能再访问 fragment manager
    }
}