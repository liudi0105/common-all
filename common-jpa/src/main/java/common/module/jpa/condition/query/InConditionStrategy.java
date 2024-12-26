package common.module.jpa.condition.query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Collection;

public class InConditionStrategy<E> implements QueryConditionStrategy<E> {

    private String field;
    private Collection<?> values;

    public InConditionStrategy(String field, Collection<?> values) {
        this.field = field;
        this.values = values;
    }

    @Override
    public Predicate apply(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return root.get(field).in(values);
    }
}
