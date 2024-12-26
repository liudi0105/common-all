package common.module.jpa.condition;

import common.module.jpa.AppPageResult;
import common.module.jpa.AppPages;
import common.module.jpa.SqlBuilder;
import common.module.util.AppJsons;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class QueryManagerImpl implements QueryManager {

    private final EntityManager entityManager;

    public QueryManagerImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private Query createQuery(SqlBuilder oldSqlBuilder) {
        Query nativeQuery = entityManager.createNativeQuery(oldSqlBuilder.toString());
        oldSqlBuilder.getParams().forEach(nativeQuery::setParameter);
        nativeQuery.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return nativeQuery;
    }


    @Override
    public <R> R queryOne(SqlBuilder oldSqlBuilder, Class<R> resultClass) {
        Query query = createQuery(oldSqlBuilder);
        Map<String, Object> resultMap = (Map<String, Object>) query.getSingleResult();
        return AppJsons.convertUnderlineMap(resultMap, resultClass);
    }

    @Override
    public <R> List<R> queryList(SqlBuilder oldSqlBuilder, Class<R> resultClass) {
        Query query = createQuery(oldSqlBuilder);
        List<Map<String, ?>> resultList = query.getResultList();
        return AppJsons.convertUnderlineMapList(resultList, resultClass);
    }

    @Override
    public <R> AppPageResult<R> queryPage(SqlBuilder oldSqlBuilder, Class<R> resultClass, Integer pageIndex, Integer pageSize) {
        AppPages.checkPageParam(pageIndex, pageSize);
        String countSql = "select count(1)  from ( " + oldSqlBuilder.toString() + " )  as total ";
        Query countQuery = entityManager.createNativeQuery(countSql).unwrap(org.hibernate.query.Query.class);
        oldSqlBuilder.getParams().forEach(countQuery::setParameter);
        long count = (Long) countQuery.getSingleResult();

        String limitSegment = " limit " + pageSize + " offset " + (pageIndex - 1) * pageSize;
        Query contentQuery = entityManager.createNativeQuery(oldSqlBuilder + limitSegment);
        oldSqlBuilder.getParams().forEach(contentQuery::setParameter);
        contentQuery.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map<String, ?>> resultList = contentQuery.getResultList();
        List<R> content = AppJsons.convertUnderlineMapList(resultList, resultClass);
        return AppPageResult.of(count, content);
    }

}
