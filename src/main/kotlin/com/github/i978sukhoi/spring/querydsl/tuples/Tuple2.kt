package com.github.i978sukhoi.spring.querydsl.tuples

import java.io.Serializable

data class Tuple2<out A, out B>(
        val v1: A,
        val v2: B
) : Serializable {
    override fun toString(): String = "($v1, $v2)"
}
