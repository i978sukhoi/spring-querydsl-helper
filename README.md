# Spring QueryDSL helper

# QRepositoy
`Write repository class without interface and also can use SimpleJpaRepository with convenience.`


[Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#core.extensions.querydsl)
already has great support for QueryDSL. but inheriting [QuerydslRepositorySupport](https://docs.spring.io/spring-data/data-jpa/docs/current/api/org/springframework/data/jpa/repository/support/QuerydslRepositorySupport.html)
or [QuerydslJpaPredicateExecutor](https://docs.spring.io/spring-data/data-jpa/docs/current/api/org/springframework/data/jpa/repository/support/QuerydslJpaPredicateExecutor.html) 
is little messy. 

QRepository is abstract class that ...
- can use `findAll`, `findOne`, `count`.
- can refer Q class by internal value `table`.
- can use `findById`, `save` by inherited [SimpleJpaRepository](https://docs.spring.io/spring-data/data-jpa/docs/current/api/org/springframework/data/jpa/repository/support/SimpleJpaRepository.html)   
(also can use [Specification](https://docs.spring.io/spring-data/data-jpa/docs/current/api/org/springframework/data/jpa/domain/Specification.html) style query)
- can use all QueryDSL functionality by internal value `querydsl`.

```kotlin
@Repository
class NoticeRepository(entityManager: EntityManager) :
     QRepository<Notice, Int, QNotice>(QNotice.notice, entityManager) {

     fun findTop10ByFilters(type: String, name:String?) = findAll(
         builder(table.type.eq(type))
             .andIf(!name.isNullOrEmpty(), { table.name.like("%$name%") })
     , table.id.desc(), 10L)
}
```

# Extensions
fallowing kotlin extension functions provided.

## BooleanBuilder.andIf
add where clue in conditionally within chain call

```kotlin
val predicate = BooleanBuilder()
    .and(table.type.eq(type))
    .andIf(category.isNotEmpty(), table.category.eq(category))
    .andIf(!name.isNullOrEmpty()) { // to avoid NPE
        table.name.eq(name) // << if name is null, NPE
    }   
```

## NumberPath.nullSafeSum
because `sum(columnName)` can be null, should use as `coalesce(sum(columnName), 0)` in many cases.

## JPQLQuery.forEach (hibernate only)
Hibernate already has [stream](https://docs.jboss.org/hibernate/orm/5.4/javadocs/org/hibernate/query/Query.html#stream--) support. but, QueryDSL does not.
This extension function
- receive lambda
- convert JPQLQuery to hibernate [Query](https://docs.jboss.org/hibernate/orm/5.4/javadocs/org/hibernate/query/Query.html)
- retrieve stream from [Query](https://docs.jboss.org/hibernate/orm/5.4/javadocs/org/hibernate/query/Query.html)
- run lambda for each rows



