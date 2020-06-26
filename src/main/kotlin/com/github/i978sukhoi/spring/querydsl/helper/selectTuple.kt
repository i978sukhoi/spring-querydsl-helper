package com.github.i978sukhoi.spring.querydsl.helper

import com.github.i978sukhoi.spring.querydsl.tuples.*
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPQLQuery
import org.springframework.core.ParameterizedTypeReference
import java.lang.reflect.ParameterizedType


/**
 * shortcut for [JPQLQuery.select] by constructor projection of [Tuple2].
 * ```kotlin
 * val groupCounts: List<Tuple2<String, Long>> = querydsl.createQuery(table)
 *      .selectTuple(table.myGroup, table.id.count())
 *      .groupBy(table.myGroup)
 *      .fetch()
 * ```
 */
@Suppress("UNCHECKED_CAST")
fun <T, A, B> JPQLQuery<T>.selectTuple(
    a: Expression<A>,
    b: Expression<B>
): JPQLQuery<Tuple2<A, B>> {
    val typeRef = object : ParameterizedTypeReference<Tuple2<A, B>>() {}
    return select(
        Projections.constructor(
            (typeRef.type as ParameterizedType).rawType as Class<Tuple2<A, B>>,
            a, b
        )
    )!!
}

/**
 * shortcut for [JPQLQuery.select] by constructor projection of [Tuple3].
 */
@Suppress("UNCHECKED_CAST")
fun <T, A, B, C> JPQLQuery<T>.selectTuple(
    a: Expression<A>,
    b: Expression<B>,
    c: Expression<C>
): JPQLQuery<Tuple3<A, B, C>> {
    val typeRef = object : ParameterizedTypeReference<Tuple3<A, B, C>>() {}
    return select(
        Projections.constructor(
            (typeRef.type as ParameterizedType).rawType as Class<Tuple3<A, B, C>>,
            a, b, c
        )
    )!!
}

/**
 * shortcut for [JPQLQuery.select] by constructor projection of [Tuple4].
 */
@Suppress("UNCHECKED_CAST")
fun <T, A, B, C, D> JPQLQuery<T>.selectTuple(
    a: Expression<A>,
    b: Expression<B>,
    c: Expression<C>,
    d: Expression<D>
): JPQLQuery<Tuple4<A, B, C, D>> {
    val typeRef = object : ParameterizedTypeReference<Tuple4<A, B, C, D>>() {}
    return select(
        Projections.constructor(
            (typeRef.type as ParameterizedType).rawType as Class<Tuple4<A, B, C, D>>,
            a, b, c, d
        )
    )!!
}

/**
 * shortcut for [JPQLQuery.select] by constructor projection of [Tuple5].
 */
@Suppress("UNCHECKED_CAST")
fun <T, A, B, C, D, E> JPQLQuery<T>.selectTuple(
    a: Expression<A>,
    b: Expression<B>,
    c: Expression<C>,
    d: Expression<D>,
    e: Expression<E>
): JPQLQuery<Tuple5<A, B, C, D, E>> {
    val typeRef = object : ParameterizedTypeReference<Tuple5<A, B, C, D, E>>() {}
    return select(
        Projections.constructor(
            (typeRef.type as ParameterizedType).rawType as Class<Tuple5<A, B, C, D, E>>,
            a, b, c, d, e
        )
    )!!
}

/**
 * shortcut for [JPQLQuery.select] by constructor projection of [Tuple6].
 */
@Suppress("UNCHECKED_CAST")
fun <T, A, B, C, D, E, F> JPQLQuery<T>.selectTuple(
    a: Expression<A>,
    b: Expression<B>,
    c: Expression<C>,
    d: Expression<D>,
    e: Expression<E>,
    f: Expression<F>
): JPQLQuery<Tuple6<A, B, C, D, E, F>> {
    val typeRef = object : ParameterizedTypeReference<Tuple6<A, B, C, D, E, F>>() {}
    return select(
        Projections.constructor(
            (typeRef.type as ParameterizedType).rawType as Class<Tuple6<A, B, C, D, E, F>>,
            a, b, c, d, e, f
        )
    )!!
}

/**
 * shortcut for [JPQLQuery.select] by constructor projection of [Tuple7].
 */
@Suppress("UNCHECKED_CAST")
fun <T, A, B, C, D, E, F, G> JPQLQuery<T>.selectTuple(
    a: Expression<A>,
    b: Expression<B>,
    c: Expression<C>,
    d: Expression<D>,
    e: Expression<E>,
    f: Expression<F>,
    g: Expression<G>
): JPQLQuery<Tuple7<A, B, C, D, E, F, G>> {
    val typeRef = object : ParameterizedTypeReference<Tuple7<A, B, C, D, E, F, G>>() {}
    return select(
        Projections.constructor(
            (typeRef.type as ParameterizedType).rawType as Class<Tuple7<A, B, C, D, E, F, G>>,
            a, b, c, d, e, f, g
        )
    )!!
}

/**
 * shortcut for [JPQLQuery.select] by constructor projection of [Tuple8].
 */
@Suppress("UNCHECKED_CAST")
fun <T, A, B, C, D, E, F, G, H> JPQLQuery<T>.selectTuple(
    a: Expression<A>,
    b: Expression<B>,
    c: Expression<C>,
    d: Expression<D>,
    e: Expression<E>,
    f: Expression<F>,
    g: Expression<G>,
    h: Expression<H>
): JPQLQuery<Tuple8<A, B, C, D, E, F, G, H>> {
    val typeRef = object : ParameterizedTypeReference<Tuple8<A, B, C, D, E, F, G, H>>() {}
    return select(
        Projections.constructor(
            (typeRef.type as ParameterizedType).rawType as Class<Tuple8<A, B, C, D, E, F, G, H>>,
            a, b, c, d, e, f, g, h
        )
    )!!
}

/**
 * shortcut for [JPQLQuery.select] by constructor projection of [Tuple9].
 */
@Suppress("UNCHECKED_CAST")
fun <T, A, B, C, D, E, F, G, H, I> JPQLQuery<T>.selectTuple(
    a: Expression<A>,
    b: Expression<B>,
    c: Expression<C>,
    d: Expression<D>,
    e: Expression<E>,
    f: Expression<F>,
    g: Expression<G>,
    h: Expression<H>,
    i: Expression<I>
): JPQLQuery<Tuple9<A, B, C, D, E, F, G, H, I>> {
    val typeRef = object : ParameterizedTypeReference<Tuple9<A, B, C, D, E, F, G, H, I>>() {}
    return select(
        Projections.constructor(
            (typeRef.type as ParameterizedType).rawType as Class<Tuple9<A, B, C, D, E, F, G, H, I>>,
            a, b, c, d, e, f, g, h, i
        )
    )!!
}
