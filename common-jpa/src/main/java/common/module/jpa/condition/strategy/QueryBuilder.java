package common.module.jpa.condition.strategy;

import common.module.util.AppReflections;
import common.module.util.model.SerializableFunction;
import jakarta.persistence.criteria.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class QueryBuilder<E> {

    private final Class<E> clazz;

    private List<ConditionBuilder<E>> conditions = new ArrayList<>();  // 存储所有的条件
    private boolean distinct = false;  // 是否使用 DISTINCT
    private Pageable pageable;  // 分页
    private Sort sort;  // 排序

    public QueryBuilder(Class<E> clazz) {
        this.clazz = clazz;
    }

    // 添加等于条件
    public <V> QueryBuilder<E> eq(SerializableFunction<E, V> function, V value) {
        if (value != null) {
            conditions.add(new ConditionBuilder<E>().eq(function, value));
        }
        return this;
    }

    // 添加 LIKE 查询条件
    public <V> QueryBuilder<E> like(SerializableFunction<E, V> function, String value) {
        if(StringUtils.isNotEmpty(value)){
            conditions.add(new ConditionBuilder<E>().like(function, value));
        }

        return this;
    }

    // 添加 BETWEEN 条件
    public <V extends Comparable<V>> QueryBuilder<E> between(SerializableFunction<E, V> function, V start, V end) {
        conditions.add(new ConditionBuilder<E>().between(function, start, end));
        return this;
    }

    // 添加 IN 条件
    public <V> QueryBuilder<E> in(SerializableFunction<E, V> field, Collection<V> values) {
        if(CollectionUtils.isNotEmpty(values)){
            conditions.add(new ConditionBuilder<E>().<V>in(field, values));
        }
        return this;
    }

    // 设置分页
    public QueryBuilder<E> page(int pageIndex, int pageSize) {
        this.pageable = PageRequest.of(pageIndex - 1, pageSize);  // PageIndex从1开始
        return this;
    }

    public QueryBuilder<E> orderBy(SerializableFunction<E, ?> field) {
        this.sort = Sort.by(AppReflections.findFieldName(field)).descending();
        return this;
    }

    // 设置 DISTINCT
    public QueryBuilder<E> distinct() {
        this.distinct = true;
        return this;
    }

    // 构建 Specification，用于 JPA 查询
    public Specification<E> toSpecification() {
        return (root, query, criteriaBuilder) -> {
            // 应用 distinct
            if (distinct) {
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();
            for (ConditionBuilder<E> condition : conditions) {
                // 获取条件对应的策略并应用
                predicates.add(condition.getStrategies().apply(root, query, criteriaBuilder));
            }

            // 应用排序
            applySorting(query, root, criteriaBuilder);

            // 返回所有条件的组合
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public void queryPage(JpaSpecificationExecutor<E> repository) {
        repository.findAll(toSpecification(), pageable);
    }

    public void query(JpaSpecificationExecutor<E> repository) {
        repository.findAll(toSpecification());
    }

    // 应用排序
    private void applySorting(CriteriaQuery<?> query, Root<E> root, CriteriaBuilder criteriaBuilder) {
        if (sort != null) {
            List<Order> orders = new ArrayList<>();
            for (Sort.Order order : sort) {
                if (order.getDirection() == Sort.Direction.ASC) {
                    orders.add(criteriaBuilder.asc(root.get(order.getProperty())));
                } else {
                    orders.add(criteriaBuilder.desc(root.get(order.getProperty())));
                }
            }
            query.orderBy(orders);
        }
    }
}
