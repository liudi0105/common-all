package common.module.jpa.condition.strategy;

import jakarta.persistence.criteria.*;
import java.util.List;

public class OrConditionStrategy<E> implements QueryConditionStrategy<E> {

    private final List<QueryConditionStrategy<E>> conditions;

    public OrConditionStrategy(List<QueryConditionStrategy<E>> conditions) {
        this.conditions = conditions;
    }

    @Override
    public Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate[] predicates = conditions.stream()
                .map(condition -> condition.apply(root, query, criteriaBuilder))
                .toArray(Predicate[]::new);
        return criteriaBuilder.or(predicates);
    }
}
