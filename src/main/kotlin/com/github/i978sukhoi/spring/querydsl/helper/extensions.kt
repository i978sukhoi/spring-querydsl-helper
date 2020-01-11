package com.github.i978sukhoi.spring.querydsl.helper

import com.querydsl.core.types.FactoryExpression
import com.querydsl.core.types.dsl.Coalesce
import com.querydsl.core.types.dsl.ComparableExpressionBase
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.jpa.FactoryExpressionTransformer
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQuery
//import com.terafunding.cloud.http.PageParam
//import com.terafunding.cloud.http.PageResult
import org.hibernate.jpa.QueryHints
import org.hibernate.query.Query
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.querydsl.QSort
import java.util.stream.Stream

/**
 * 결과가 없다면 sum() 결과는 null 일 수 있는데 이를 null 이 아닌 값([nullValue])으로 가져오기 위해
 * `coalesce(sum(columnName), 0)` 을 취한다.
 * @param nullValue 결과가 null 일 때 취할 값. 기본값은 0.
 */
fun <T> NumberPath<T>.nullSafeSum(nullValue: T? = null) where T : Number, T : Comparable<*> =
    Coalesce(this.type, this.sum(), Expressions.constant(nullValue ?: 0))

/**
 * [com.querydsl.jpa.JPQLQuery]를 [org.hibernate.query.Query]로 변환하여 결과 데이터를 [Stream]으로 받아와서 [action]을 실행시킨다.
 */
fun <T> JPQLQuery<T>.forEach(limit: Int, action: (T) -> Unit) {
    // create hibernate query
    @Suppress("UNCHECKED_CAST")
    val query = (this as JPAQuery<T>).createQuery() as Query<T>

    /**
     * 원래는 [JPAQuery.createQuery]를 통해 [org.hibernate.transform.ResultTransformer]가 등록되어야 하나
     * 실제 생성된 객체([org.hibernate.query.Query])는 [org.hibernate.jpa.HibernateQuery]가 아니므로 등록되지 않는다.
     * QueryDSL의 버그로 봐야할듯...
     *
     * @see com.querydsl.jpa.HibernateHandler.transform
     */
    // set projection if required
    val projection = this.metadata.projection
    if (projection != null && projection is FactoryExpression)
        query.setResultTransformer(FactoryExpressionTransformer(projection))

    // set limit
    if (limit > 0) query.maxResults = limit

    // set streaming hint to mysql
    query.setHint(QueryHints.HINT_FETCH_SIZE, Int.MIN_VALUE)

    // retrieve stream
    val stream = query.stream() as Stream<T>

    // do action
    stream.forEach(action)
}

fun <T> JPQLQuery<T>.forEach(action: (T) -> Unit) = forEach(0, action)
