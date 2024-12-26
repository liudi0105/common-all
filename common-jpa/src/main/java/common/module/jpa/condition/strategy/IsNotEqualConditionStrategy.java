package common.module.jpa.condition.strategy;

import jakarta.persistence.criteria.*;

public class IsNotEqualConditionStrategy<E> implements QueryConditionStrategy<E> {

    private final String field;
    private final Object value;

    public IsNotEqualConditionStrategy(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.notEqual(root.get(field), value);
    }
}
