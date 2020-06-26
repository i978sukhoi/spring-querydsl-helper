package com.github.i978sukhoi.spring.querydsl.tuples

import java.io.Serializable

data class Tuple6<out A, out B, out C, out D, out E, out F>(
        val v1: A,
        val v2: B,
        val v3: C,
        val v4: D,
        val v5: E,
        val v6: F
) : Serializable {
    override fun toString(): String = "($v1, $v2, $v3, $v4, $v5, $v6)"
}
