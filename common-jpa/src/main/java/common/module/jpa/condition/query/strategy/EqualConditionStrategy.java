package common.module.jpa.condition.query.strategy;

import common.module.jpa.condition.query.QueryConditionStrategy;
import jakarta.persistence.criteria.*;

public class EqualConditionStrategy<E> implements QueryConditionStrategy<E> {

    private String field;
    private Object value;

    public EqualConditionStrategy(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(root.get(field), value);
    }
}
