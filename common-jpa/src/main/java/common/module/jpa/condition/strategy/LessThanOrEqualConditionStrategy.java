package common.module.jpa.condition.strategy;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class LessThanOrEqualConditionStrategy<E> implements QueryConditionStrategy<E> {

    private final String field;
    private final Comparable<?> value;

    public LessThanOrEqualConditionStrategy(String field, Comparable<?> value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.lessThanOrEqualTo(root.get(field), (Comparable) value);
    }
}
