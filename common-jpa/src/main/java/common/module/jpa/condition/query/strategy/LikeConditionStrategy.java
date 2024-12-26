package common.module.jpa.condition.query.strategy;

import common.module.jpa.condition.query.QueryConditionStrategy;
import jakarta.persistence.criteria.*;

public class LikeConditionStrategy<E> implements QueryConditionStrategy<E> {

    private String field;
    private String value;

    public LikeConditionStrategy(String field, String value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.like(root.get(field), "%" + value + "%");
    }
}
