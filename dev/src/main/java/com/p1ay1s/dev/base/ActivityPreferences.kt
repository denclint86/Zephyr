package com.p1ay1s.dev.base

interface ActivityPreferences {
    @Deprecated("use two clicks to exit listener!")
    interface TwoBackPressToExitListener {
        fun twoBackPressToExit()
    }

    interface TwoClicksListener {
        fun twoClicksToExit()
    }

    @Deprecated("no use")
    interface OnBackPressListener {
        fun onBackPress()
    }
}