package com.github.i978sukhoi.spring.querydsl.tuples

import java.io.Serializable

data class Tuple3<out A, out B, out C>(
        val v1: A,
        val v2: B,
        val v3: C
) : Serializable {
    override fun toString(): String = "($v1, $v2, $v3)"
}
