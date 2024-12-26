package common.module.jpa.condition.query;

import jakarta.persistence.criteria.*;

public class BetweenConditionStrategy<E> implements QueryConditionStrategy<E> {
    private String field;
    private Object start;
    private Object end;

    public BetweenConditionStrategy(String field, Object start, Object end) {
        this.field = field;
        this.start = start;
        this.end = end;
    }

    @Override
    public Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.between(root.get(field), (Comparable) start, (Comparable) end);
    }
}
