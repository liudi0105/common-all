package common.module.jpa.condition;

import common.module.jpa.AppPageResult;
import common.module.jpa.SqlBuilder;
import common.module.jpa.condition.strategy.QueryBuilder;

import java.util.List;

public interface QueryManager {
    <E> List<E> query(QueryBuilder<E> queryBuilder);

    <E, D> List<D> query(QueryBuilder<E> queryBuilder, Class<D> toClazz);

    <E, D> AppPageResult<D> queryPage(QueryBuilder<E> queryBuilder, Class<D> toClazz);

    <E> AppPageResult<E> queryPage(QueryBuilder<E> queryBuilder);

    <E> E queryOne(QueryBuilder<E> queryBuilder);

    <E, D> D queryOne(QueryBuilder<E> queryBuilder, Class<D> toClazz);

    <R> R queryOne(SqlBuilder oldSqlBuilder, Class<R> resultClass);

    <R> List<R> queryList(SqlBuilder oldSqlBuilder, Class<R> resultClass);

    <R> AppPageResult<R> queryPage(SqlBuilder oldSqlBuilder, Class<R> resultClass, Integer pageIndex, Integer pageSize);
}
