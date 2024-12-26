package common.module.jpa.condition.query;

import common.module.util.model.SerializableFunction;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class QueryBuilder<E> {

    private List<ConditionBuilder<E>> conditions = new ArrayList<>();  // 存储所有的条件
    private boolean distinct = false;  // 是否使用 DISTINCT
    private Pageable pageable;  // 分页
    private Sort sort;  // 排序

    // 添加等于条件
    public <V> QueryBuilder<E> eq(SerializableFunction<E, V> function, V value) {
        conditions.add(new ConditionBuilder<E>().eq(function, value));
        return this;
    }

    // 添加 LIKE 查询条件
    public <V> QueryBuilder<E> like(SerializableFunction<E, V> function, String value) {
        conditions.add(new ConditionBuilder<E>().like(function, value));
        return this;
    }

    // 添加 BETWEEN 条件
    public <V extends Comparable<V>> QueryBuilder<E> between(SerializableFunction<E, V> function, V start, V end) {
        conditions.add(new ConditionBuilder<E>().between(function, start, end));
        return this;
    }

    // 添加 IN 条件
    public <V> QueryBuilder<E> in(SerializableFunction<E, V> field, Collection<V> values) {
        conditions.add(new ConditionBuilder<E>().<V>in(field, values));
        return this;
    }

    // 设置分页
    public QueryBuilder<E> page(int pageIndex, int pageSize) {
        this.pageable = PageRequest.of(pageIndex - 1, pageSize);  // PageIndex从1开始
        return this;
    }

    // 设置排序
    public QueryBuilder<E> sort(Sort sort) {
        this.sort = sort;
        return this;
    }

    // 设置 DISTINCT
    public QueryBuilder<E> distinct() {
        this.distinct = true;
        return this;
    }

    // 构建 Specification，用于 JPA 查询
    public Specification<E> build() {
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

            // 应用分页
            applyPagination(query);

            // 返回所有条件的组合
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // 应用分页，利用Spring Data JPA的Pageable对象来进行分页
    private void applyPagination(CriteriaQuery<?> query) {
        if (pageable != null) {
        }
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
