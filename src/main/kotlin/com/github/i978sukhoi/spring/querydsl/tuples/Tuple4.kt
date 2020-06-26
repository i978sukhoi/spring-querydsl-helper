package com.github.i978sukhoi.spring.querydsl.tuples

import java.io.Serializable

data class Tuple4<out A, out B, out C, out D>(
        val v1: A,
        val v2: B,
        val v3: C,
        val v4: D
) : Serializable {
    override fun toString(): String = "($v1, $v2, $v3, $v4)"
}
