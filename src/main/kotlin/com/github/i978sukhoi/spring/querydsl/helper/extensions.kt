package com.github.i978sukhoi.spring.querydsl.helper

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.FactoryExpression
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.Coalesce
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.jpa.FactoryExpressionTransformer
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQuery
import org.hibernate.jpa.QueryHints
import org.hibernate.query.Query
import java.util.stream.Stream

/**
 * 보통 입력된 값에 따라 조건적으로 where 절이 추가되기 때문에 이를 chain call 형태로 이용할 수 있게 하기 위한 확장.
 *
 * @param condition 이 조건이 true 일 때 만 [predicate] 가 [BooleanBuilder] 에 추가된다.
 * @param predicate 추가될
 */
fun BooleanBuilder.andIf(condition: Boolean, predicate: Predicate): BooleanBuilder {
    if (condition) and(predicate)
    return this
}

/**
 * [Predicate] 를 직접 입력받는 [andIf] 를 이용할 때는 항상 predicate 가 생성(평가)될 수 밖에 없으므로
 * 이 비용을 무시하기 힘든 경우라면 lambda 를 입력받아서 [condition] 에 따라 predicate 생성이 불필요한 상황을 지원한다.
 * ```
 *      .andIf(!name.isNullOrEmpty(), table.name.eq(name)) // 이 경우 name 이 null 이라면 eq() 에서 NPE 가 발생한다.
 *      .andIf(!name.isNullOrEmpty(), { table.name.eq(name) }) // lambda 로 분리하여 NPE 를 회피한다.
 * ```
 *
 * @param condition 이 조건이 true 일 때 만 [predicateLambda] 를 실행하여 [BooleanBuilder] 에 추가된다.
 * @param predicateLambda [Predicate]를 return 하는 lambda function
 */
fun BooleanBuilder.andIf(condition: Boolean, predicateLambda: () -> Predicate): BooleanBuilder {
    if (condition) and(predicateLambda())
    return this
}

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
