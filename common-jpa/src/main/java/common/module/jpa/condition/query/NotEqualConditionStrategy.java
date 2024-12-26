package common.module.jpa.condition.query;

import jakarta.persistence.criteria.*;

public class NotEqualConditionStrategy<E> implements QueryConditionStrategy<E> {

    private final String field;
    private final Object value;

    public NotEqualConditionStrategy(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.notEqual(root.get(field), value);
    }
}
