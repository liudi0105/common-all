package common.module.jpa.condition.query;

import jakarta.persistence.criteria.*;

public class GreaterThanConditionStrategy<E> implements QueryConditionStrategy<E> {

    private final String field;
    private final Comparable<?> value;

    public GreaterThanConditionStrategy(String field, Comparable<?> value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.greaterThan(root.get(field), (Comparable) value);
    }
}
