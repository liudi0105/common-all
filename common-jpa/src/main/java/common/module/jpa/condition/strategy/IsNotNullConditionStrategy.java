package common.module.jpa.condition.strategy;

import jakarta.persistence.criteria.*;

public class IsNotNullConditionStrategy<E> implements QueryConditionStrategy<E> {

    private final String field;

    public IsNotNullConditionStrategy(String field) {
        this.field = field;
    }

    @Override
    public Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isNotNull(root.get(field));
    }
}
