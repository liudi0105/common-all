package common.module.jpa.condition;

import common.module.jpa.AppJpaEntity;
import common.module.jpa.AppPageResult;
import common.module.jpa.AppPages;
import common.module.jpa.SqlBuilder;
import common.module.jpa.condition.strategy.QueryBuilder;
import common.module.util.AppBeans;
import common.module.util.AppJsons;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class QueryManagerImpl implements QueryManager {

    private final EntityManager entityManager;

    public QueryManagerImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <E extends AppJpaEntity> PageImpl<E> queryPage(QueryBuilder<E> queryBuilder, Pageable pageable, Class<E> clazz) {
        Wrapper<E> criteriaQuery = buildQuery(queryBuilder, clazz);

        long l = countTotal(criteriaQuery.specification, clazz);

        // 获取查询结果
        List<E> result = applyPagination(criteriaQuery.criteriaQuery, criteriaQuery.root, criteriaQuery.criteriaBuilder, pageable);
        return new PageImpl<E>(result, pageable, l);
    }

    private <E> List<E> applyPagination(CriteriaQuery<E> criteriaQuery, Root<E> root, CriteriaBuilder criteriaBuilder, Pageable pageable) {
        TypedQuery<E> query = entityManager.createQuery(criteriaQuery);

        // 设置分页
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // 执行查询并返回结果
        return query.getResultList();
    }

    /**
     * 计算总记录数
     */
    private <E> long countTotal(Specification<E> specification, Class<E> clazz) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);

        Root<E> root = countQuery.from(clazz);

        // 应用 Specification 生成的查询条件
        Predicate predicate = specification.toPredicate(root, countQuery, criteriaBuilder);
        countQuery.where(predicate);

        countQuery.select(criteriaBuilder.count(root));  // 选择计数

        // 执行查询并返回结果
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    @Override
    public <E> List<E> query(QueryBuilder<E> queryBuilder) {
        CriteriaQuery<E> criteriaQuery = buildQuery(queryBuilder, queryBuilder.getClazz()).getCriteriaQuery();
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public <E, D> List<D> query(QueryBuilder<E> queryBuilder, Class<D> toClazz) {
        return AppBeans.convertList(query(queryBuilder, queryBuilder.getClazz()), toClazz);
    }

    @Override
    public <E, D> AppPageResult<D> queryPage(QueryBuilder<E> queryBuilder, Class<D> toClazz) {
        return queryPage(queryBuilder)
                .map(v -> AppBeans.convert(v, toClazz));
    }

    @Override
    public <E> AppPageResult<E> queryPage(QueryBuilder<E> queryBuilder) {
        List<E> d = entityManager.createQuery(buildPageQuery(queryBuilder))
                .setFirstResult((int) queryBuilder.getPageable().getOffset())
                .setMaxResults(queryBuilder.getPageable().getPageSize())
                .getResultList();

        Long l = countTotal(queryBuilder);
        return AppPageResult.of(l, d);
    }

    @Getter
    @AllArgsConstructor
    static class Wrapper<E> {
        private CriteriaQuery<E> criteriaQuery;
        private Root<E> root;
        private CriteriaBuilder criteriaBuilder;
        private Specification<E> specification;
    }

    @Override
    public <E> E queryOne(QueryBuilder<E> queryBuilder) {
        return entityManager.createQuery(buildPageQuery(queryBuilder))
                .getSingleResult();
    }

    @Override
    public <E, D> D queryOne(QueryBuilder<E> queryBuilder, Class<D> toClazz) {
        return AppBeans.convert(queryOne(queryBuilder), toClazz);
    }

    private <E> Long countTotal(QueryBuilder<E> queryBuilder) {
        // 构建 Specification
        Specification<E> specification = queryBuilder.toSpecification();

        // 获取 CriteriaBuilder 和 CriteriaQuery
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // 根实体
        Root<E> root = criteriaQuery.from(queryBuilder.getClazz());

        // 将 Specification 应用到 CriteriaQuery 上
        Predicate predicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(predicate);
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    private <E> CriteriaQuery<E> buildPageQuery(QueryBuilder<E> queryBuilder) {
        // 构建 Specification
        Specification<E> specification = queryBuilder.toSpecification();

        // 获取 CriteriaBuilder 和 CriteriaQuery
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(queryBuilder.getClazz());

        // 根实体
        Root<E> root = criteriaQuery.from(queryBuilder.getClazz());

        // 将 Specification 应用到 CriteriaQuery 上
        Predicate predicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder);
        criteriaQuery.where(predicate);
        return criteriaQuery;
    }

    private <E> Wrapper<E> buildQuery(QueryBuilder<E> queryBuilder, Class<E> clazz) {
        // 构建 Specification
        Specification<E> specification = queryBuilder.toSpecification();

        // 获取 CriteriaBuilder 和 CriteriaQuery
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(clazz);

        // 根实体
        Root<E> root = criteriaQuery.from(clazz);

        // 将 Specification 应用到 CriteriaQuery 上
        Predicate predicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder);
        criteriaQuery.where(predicate);
        return new Wrapper<E>(criteriaQuery, root, criteriaBuilder, specification);
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
