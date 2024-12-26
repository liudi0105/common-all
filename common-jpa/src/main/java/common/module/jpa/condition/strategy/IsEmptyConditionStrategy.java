package common.module.jpa.condition.strategy;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Strategy for the "IS EMPTY" condition.
 * It checks if a collection field is empty.
 */
public class IsEmptyConditionStrategy<E> implements QueryConditionStrategy<E> {

    private final String field;

    public IsEmptyConditionStrategy(String field) {
        this.field = field;
    }

    @Override
    public Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isEmpty(root.get(field));
    }
}
