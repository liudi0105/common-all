package common.module.jpa.condition.query;

import jakarta.persistence.criteria.*;

public class NotNullConditionStrategy<E> implements QueryConditionStrategy<E> {

    private final String field;

    public NotNullConditionStrategy(String field) {
        this.field = field;
    }

    @Override
    public Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isNotNull(root.get(field));
    }
}
