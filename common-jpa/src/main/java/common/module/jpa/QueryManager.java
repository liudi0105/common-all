package common.module.jpa;

import java.util.List;

public interface QueryManager {
    <R> R queryOne(SqlBuilder oldSqlBuilder, Class<R> resultClass);

    <R> List<R> queryList(SqlBuilder oldSqlBuilder, Class<R> resultClass);

    <R> AppPageResult<R> queryPage(SqlBuilder oldSqlBuilder, Class<R> resultClass, Integer pageIndex, Integer pageSize);
}
