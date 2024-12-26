package common.module.jpa.condition.query.strategy;

import common.module.jpa.condition.query.QueryConditionStrategy;
import jakarta.persistence.criteria.*;

/**
 * Strategy for the "IS NOT EMPTY" condition.
 * It checks if a collection field is not empty.
 */
public class IsNotEmptyConditionStrategy<E> implements QueryConditionStrategy<E> {

    private final String field;

    public IsNotEmptyConditionStrategy(String field) {
        this.field = field;
    }

    @Override
    public Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isNotEmpty(root.get(field));
    }
}
