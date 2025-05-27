package com.zephyr.log.interfaces

internal interface IBufferCtrl {
    fun pop(): String
    fun add(string: String)
}