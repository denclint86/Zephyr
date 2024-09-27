package com.p1ay1s.dev.base

interface ActivityPreferences {
    interface TwoBackPressToExitListener {
        fun twoBackPressToExit()
    }

    interface OnBackPressListener {
        fun onBackPress()
    }
}