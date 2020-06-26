package com.github.i978sukhoi.spring.querydsl.tuples

fun <T> Tuple2<T, T>.toList(): List<T> = listOf(v1, v2)
fun <T> Tuple3<T, T, T>.toList(): List<T> = listOf(v1, v2, v3)
fun <T> Tuple4<T, T, T, T>.toList(): List<T> = listOf(v1, v2, v3, v4)
fun <T> Tuple5<T, T, T, T, T>.toList(): List<T> = listOf(v1, v2, v3, v4, v5)
fun <T> Tuple6<T, T, T, T, T, T>.toList(): List<T> = listOf(v1, v2, v3, v4, v5, v6)
fun <T> Tuple7<T, T, T, T, T, T, T>.toList(): List<T> = listOf(v1, v2, v3, v4, v5, v6, v7)
fun <T> Tuple8<T, T, T, T, T, T, T, T>.toList(): List<T> = listOf(v1, v2, v3, v4, v5, v6, v7, v8)
fun <T> Tuple9<T, T, T, T, T, T, T, T, T>.toList(): List<T> = listOf(v1, v2, v3, v4, v5, v6, v7, v8, v9)
