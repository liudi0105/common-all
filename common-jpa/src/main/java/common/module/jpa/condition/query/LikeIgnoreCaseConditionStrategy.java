package common.module.jpa.condition.query;

import jakarta.persistence.criteria.*;

public class LikeIgnoreCaseConditionStrategy<E> implements QueryConditionStrategy<E> {

    private final String field;
    private final String value;

    public LikeIgnoreCaseConditionStrategy(String field, String value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.like(criteriaBuilder.lower(root.get(field)), "%" + value.toLowerCase() + "%");
    }
}
