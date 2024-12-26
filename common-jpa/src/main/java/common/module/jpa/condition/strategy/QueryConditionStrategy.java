package common.module.jpa.condition.strategy;

import jakarta.persistence.criteria.*;

public interface QueryConditionStrategy<E> {
    Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder);
}
