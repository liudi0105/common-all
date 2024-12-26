package common.module.jpa.condition.query.strategy;

import common.module.jpa.condition.query.QueryConditionStrategy;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Strategy for the "IS NULL" condition
 */
public class IsNullConditionStrategy<E> implements QueryConditionStrategy<E> {

    private final String field;

    public IsNullConditionStrategy(String field) {
        this.field = field;
    }

    @Override
    public Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isNull(root.get(field));
    }
}
