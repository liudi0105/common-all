package common.module.jpa.condition.strategy;

import jakarta.persistence.criteria.*;

public class LessThanConditionStrategy<E> implements QueryConditionStrategy<E> {

    private final String field;
    private final Comparable<?> value;

    public LessThanConditionStrategy(String field, Comparable<?> value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.lessThan(root.get(field), (Comparable) value);
    }
}
