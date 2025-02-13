package com.zephyr.extension.ui.fragmenthost

import com.zephyr.global_values.TAG
import com.zephyr.log.logE
import java.util.Stack

open class PairStack<A, B> : Stack<Pair<A, B>>() {


    @JvmName("findPair1")
    fun findPair(a: A?): Pair<A?, B?>? {
        for (pair in elements()) {
            if (pair.first == a) return pair
        }
        logE(TAG, "$a 不在 ${elements().toList()} 中")
        return null
    }

    @JvmName("findPair2")
    fun findPair(b: B?): Pair<A?, B?>? {
        for (pair in elements()) {
            if (pair.second == b) return pair
        }
        logE(TAG, "$b 不在 ${elements()} 中")
        return null
    }

    fun getAllFirst(): List<A?> {
        val result = mutableListOf<A?>()
        for (pair in elements()) {
            result.add(pair.first)
        }
        return result
    }

    fun getAllSecond(): List<B?> {
        val result = mutableListOf<B?>()
        for (pair in elements()) {
            result.add(pair.second)
        }
        return result
    }

    override fun peek(): Pair<A, B>? = try {
        super.peek()
    } catch (_: Exception) {
        null
    }

    override fun pop(): Pair<A, B>? = try {
        super.pop()
    } catch (_: Exception) {
        null
    }
}