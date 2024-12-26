package common.module.jpa.condition.strategy;

import jakarta.persistence.criteria.*;
import java.util.Collection;

/**
 * Strategy for the "NOT IN" condition
 */
public class IsNotInConditionStrategy<E> implements QueryConditionStrategy<E> {

    private final String field;
    private final Collection<?> values;

    public IsNotInConditionStrategy(String field, Collection<?> values) {
        this.field = field;
        this.values = values;
    }

    @Override
    public Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.not(root.get(field).in(values));
    }
}
