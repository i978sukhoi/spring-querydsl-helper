package com.github.i978sukhoi.spring.querydsl.helper


import com.querydsl.core.BooleanBuilder
import com.querydsl.core.NonUniqueResultException
import com.querydsl.core.types.Expression
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.PathBuilderFactory
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPADeleteClause
import com.querydsl.jpa.impl.JPAUpdateClause
import org.springframework.dao.IncorrectResultSizeDataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.support.Querydsl
import org.springframework.data.jpa.repository.support.QuerydslJpaPredicateExecutor
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.support.PageableExecutionUtils
import java.util.*
import javax.persistence.EntityManager

/**
 * QuerydslRepositorySupport 를 직접 상속받거나 QuerydslJpaPredicateExecutor 를 구현한 interface 를 통해 spring 구현체를 사용하는 방식이
 * 코드 타이핑 양이나 코드 보기에 불편함이 있어서 ...
 * - 자주 쓰는 [findAll], [findOne], [count] 등의 메소드를 추가 함.
 * - Q class 를 내부 변수([table])로 갖도록 하여 매번 변수생성을 하지 않아도 되도록 함.
 * - [BooleanBuilder] 의 확장함수 [andIf] 를 통해 조건적으로 [Predicate]를 추가하기 용이하도록 함.
 * - [SimpleJpaRepository] 를 상속받아서 [findById], [save] 를 바로 사용 할 수 있도록 함. (부가적으로 [Specification] 스타일의 쿼리도 사용가능)
 * - protected 변수 [querydsl] 를 이용하 QueryDSL의 모든 기능을 이용 할 수 있음.
 * - 실질적으로 가장 자주 쓰이게 되는 Q class 의 instance 로 [table] 변수를 둬서 편리함을 도모함.
 *
 *
 * 예)
 * ```
 * @Repository
 * class NoticeRepository(entityManager: EntityManager) :
 *      TeraRepository<Notice, Int, QNotice>(QNotice.notice, entityManager) {
 *
 *      fun findTop10ByFilters(name:String?, type: String?) = findAll(
 *          builder()
 *              .andIf(!name.isNullOrEmpty(), table.name.eq(name))
 *              .andIf(!type.isNullOrEmpty(), table.type.eq(type))
 *      , table.id.desc(), 10L)
 * }
 * ```
 *
 * @param T 원래의 Entity 클래스
 * @param ID Entity 의 `@Id` 멤버필드의 타입. ex) Int, Long ...
 * @param Q QueryDSL 에 의해 생성된 Q 클래스
 * @param table Q 클래스의 실제 인스턴스
 * @param entityManager Spring 에서 inject 받음
 *
 * @see SimpleJpaRepository
 * @see QuerydslRepositorySupport
 * @see QuerydslJpaPredicateExecutor
 */
@Suppress("UNCHECKED_CAST")
abstract class QRepository<T, ID, Q : EntityPathBase<T>>(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    protected val table: Q,
    protected val entityManager: EntityManager
) : SimpleJpaRepository<T, ID>(table.type!! as Class<T>, entityManager) {

    @Suppress("SpellCheckingInspection", "LeakingThis")
    protected val querydsl = Querydsl(entityManager, PathBuilderFactory().create(table.type))

    /**
     * select([S]) 표현을 직접 지정하여 임의의 Entity([F])를 기반으로 하는 쿼리를 생성한다.
     *
     * 예)
     * ```
     * val maxId = select(QSomeTable.someTable.amount.max(), QSomeTable.someTable).fetchOne() ?: 0L
     * ```
     * [fromEntityPath]가 null 이면 [table]로 대신한다.
     * ```
     * val maxId = select(table.amount.max()).fetchOne() ?: 0L
     * ```
     *
     * @param selectExpression select 절에 사용할 [Expression] ex) table.amount.max()
     * @param fromEntityPath from 절에 사용할 테이블의 [EntityPathBase]. 보통은 QClass 이고 null 이라면 [table]로 대신한다.
     */
    protected fun <S, F> select(selectExpression: Expression<S>, fromEntityPath: EntityPathBase<F>? = null) =
        querydsl.createQuery(fromEntityPath ?: table).select(selectExpression)!!

    /**
     * [Q]클래스를 기반으로 하는 쿼리를 생성한다.
     *
     * 만약 부득이하게 ([T]와 맵핑된)대상 테이블이 아닌 테이블을 시작으로 쿼리를 구성해야 한다면 [select]를 이용하거나
     * [querydsl] 변수를 이용하여 [Querydsl.createQuery]를 직접 호출하면 된다.
     *
     * 예)
     * ```
     *  from({ // 여기서 it 은 JPQLQuery 객체이다.
     *          it.leftJoin(table.relatedOther, QRelatedOther.relatedOther).fetchJoin()
     *      })
     *      .where( ... )
     *      .fetch()
     * ```
     *
     * @param queryComposer [JPQLQuery]을 파라미터로 하는 lambda 를 넘기면 호출해준다. 주로 join 등을 처리하려고 할때 쓰임
     * @see QuerydslRepositorySupport.from
     */
    fun from(queryComposer: ((query: JPQLQuery<T>) -> Unit)? = null): JPQLQuery<T> {
        val query = select(table, table)
        if (queryComposer != null) queryComposer(query)
        return query
    }

    /**
     * where 절을 구성하기위한 [BooleanBuilder]. 이 메소드를 사용하지 않고 그냥 [BooleanBuilder] 를 생성하여 써도 무방하다.
     *
     * 예)
     * ```
     *  val list = findAll(
     *      builder(table.board.eq("notice"))
     *          .andIf(!category.isNullOrEmpty(), table.category.eq(category))
     *          .andIf(!keyword.isNullOrEmpty(), table.title.like("%$keyword%"))
     *  , table.id.desc())
     * ```
     * 만약 가변적인 조건을 구성하는 경우가 아니라면 그냥 [JPQLQuery.where]를 이용하는게 좋다.
     * ```
     *  val list = from().where(
     *      table.type.eq("..."),
     *      table.status.eq("...")
     *  ).orderBy(table.id.desc()).fetch()
     * ```
     * @param initial 초기조건. null 을 기본값으로 하기 때문에 이 파라미터 없이 호출해도 된다.
     */
    fun builder(initial: Predicate? = null) = (if (initial == null) BooleanBuilder()
    else BooleanBuilder().and(initial))!!

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
     *
     * @param condition 이 조건이 true 일 때 만 [predicateLambda] 를 실행하여 [BooleanBuilder] 에 추가된다.
     * @param predicateLambda [Predicate]를 return 하는 lambda function
     */
    fun BooleanBuilder.andIf(condition: Boolean, predicateLambda: () -> Predicate): BooleanBuilder {
        if (condition) and(predicateLambda())
        return this
    }

    /**
     * @see QuerydslRepositorySupport.delete
     */
    fun delete() = JPADeleteClause(entityManager, table)

    /**
     * @see QuerydslRepositorySupport.update
     */
    fun update() = JPAUpdateClause(entityManager, table)

    /**
     * @param queryComposer [JPQLQuery]을 파라미터로 하는 lambda 를 넘기면 호출해준다. 주로 join 등을 처리하려고 할때 쓰임
     * @see QuerydslJpaPredicateExecutor.findAll
     */
    fun findAll(
        predicate: Predicate, order: OrderSpecifier<*>? = null, limit: Long = 0,
        queryComposer: ((query: JPQLQuery<T>) -> Unit)? = null
    ): List<T> {
        val query = from(queryComposer).where(predicate)
        if (order != null) query.orderBy(order)
        if (limit > 0) query.limit(limit)
        return query.fetch() ?: emptyList()
    }

    fun findAll(
        predicate: Predicate, orders: List<OrderSpecifier<*>>, limit: Long = 0,
        queryComposer: ((query: JPQLQuery<T>) -> Unit)? = null
    ): List<T> {
        val query = from(queryComposer).where(predicate)
            .orderBy(*orders.toTypedArray())
        if (limit > 0) query.limit(limit)
        return query.fetch() ?: emptyList()
    }

    fun findAll(
        predicate: Predicate, pageable: Pageable,
        queryComposer: ((query: JPQLQuery<T>) -> Unit)? = null
    ): Page<T> {
        val query = from(queryComposer).where(predicate)
        return PageableExecutionUtils.getPage(
            querydsl.applyPagination<T>(pageable, query).fetch(),
            pageable
        ) { query.fetchCount() }
    }

    fun <O> findAll(
        query: JPQLQuery<O>, pageable: Pageable
    ) = PageableExecutionUtils.getPage(
        querydsl.applyPagination<O>(pageable, query).fetch(),
        pageable
    ) { query.fetchCount() }

    /**
     * @param queryComposer [JPQLQuery]을 파라미터로 하는 lambda 를 넘기면 호출해준다. 주로 join 등을 처리하려고 할때 쓰임
     * @see QuerydslJpaPredicateExecutor.findOne
     */
    fun findOne(predicate: Predicate, queryComposer: ((query: JPQLQuery<T>) -> Unit)? = null) =
        try {
            Optional.ofNullable(from(queryComposer).where(predicate).fetchOne())
        } catch (ex: NonUniqueResultException) {
            throw IncorrectResultSizeDataAccessException(ex.message ?: "", 1, ex)
        }

    /**
     *
     * @see QuerydslJpaPredicateExecutor.count
     */
    fun count(predicate: Predicate, queryComposer: ((query: JPQLQuery<T>) -> Unit)? = null) =
        from(queryComposer).where(predicate).fetchCount()

    /**
     * @see JPQLQuery.forEach
     */
    fun forEach(
        predicate: Predicate, orders: List<OrderSpecifier<*>>, limit: Int = 0,
        queryComposer: ((query: JPQLQuery<T>) -> Unit)? = null,
        action: (T) -> Unit
    ) = from(queryComposer)
        .where(predicate)
        .orderBy(*orders.toTypedArray())
        .forEach(limit, action)
}
