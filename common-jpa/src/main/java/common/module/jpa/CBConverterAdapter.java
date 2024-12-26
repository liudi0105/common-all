package common.module.jpa;

import common.module.util.errors.AppError;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;

public class CBConverterAdapter implements AppCBConverter.Converter {

    @Override
    public <E> Specification<E> toSpecification(ConditionBuilder<E> conditionBuilder) {
        return (root, query, criteriaBuilder) -> toPredicate(conditionBuilder, root, criteriaBuilder);
    }

    @Override
    public <E> Specification<E> toSpecification(QueryBuilder<E> queryBuilder) {
        return (root, query, criteriaBuilder) -> toPredicate(queryBuilder, root, query, criteriaBuilder);
    }

    @Override
    public <E> Predicate toPredicate(QueryBuilder<E> qb, Root<E> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

        if (qb.getDistinct() != null) {
            criteriaQuery.distinct(qb.getDistinct());
        }

        if (qb.getOrderBy() != null) {
            Order[] orders = Arrays.stream(qb.getOrderBy())
                    .map(v -> qb.getAsc() == null || qb.getAsc() ? criteriaBuilder.asc(root.get(v)) : criteriaBuilder.desc(root.get(v)))
                    .toArray(Order[]::new);
            criteriaQuery.orderBy(orders);
        }

        if (qb.getWhere() != null) {
            Predicate predicate = toPredicate(qb.getWhere(), root, criteriaBuilder);
            criteriaQuery.where(predicate);
        }

        if (qb.getSelect() != null) {
            Selection<String>[] selections = Arrays
                    .stream(qb.getSelect())
                    .map(root::get)
                    .toArray(Selection[]::new);
            criteriaQuery.multiselect(selections);
        }

        return criteriaQuery.getRestriction();
    }

    @Override
    public <E> Predicate toPredicate(ConditionBuilder<E> cb, Root<E> root, CriteriaBuilder criteriaBuilder) {
        return switch (cb.getType()) {
            case NOT_EQUAL -> criteriaBuilder.notEqual(root.get(cb.getField()), cb.getArgs().get(0));
            case EQUAL -> criteriaBuilder.equal(root.get(cb.getField()), cb.getArgs().get(0));
            case LIKE -> criteriaBuilder.like(root.get(cb.getField()), (String) cb.getArgs().get(0));
            case OR ->
                    criteriaBuilder.or(Arrays.stream(cb.orCondition).map(v -> toPredicate(v, root, criteriaBuilder)).toArray(Predicate[]::new));
            case AND ->
                    criteriaBuilder.and(Arrays.stream(cb.andCondition).map(v -> toPredicate(v, root, criteriaBuilder)).toArray(Predicate[]::new));
            case IS_NULL -> criteriaBuilder.isNull(root.get(cb.getField()));
            case NOT_NULL -> criteriaBuilder.isNotNull(root.get(cb.getField()));
            case IS_EMPTY -> criteriaBuilder.isEmpty(root.get(cb.getField()));
            case NOT_EMPTY -> criteriaBuilder.isNotEmpty(root.get(cb.getField()));
            case BETWEEN ->
                    criteriaBuilder.between(root.get(cb.getField()), (Comparable) cb.getArgs().get(0), (Comparable) cb.getArgs().get(1));
            case GREATER_THAN, AFTER ->
                    criteriaBuilder.greaterThan(root.get(cb.getField()), (Comparable) cb.getArgs().get(0));
            case GREATER_EQUAL ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get(cb.getField()), (Comparable) cb.getArgs().get(0));
            case LESS_EQUAL ->
                    criteriaBuilder.lessThanOrEqualTo(root.get(cb.getField()), (Comparable) cb.getArgs().get(0));
            case LESS_THAN, BEFORE ->
                    criteriaBuilder.lessThan(root.get(cb.getField()), (Comparable) cb.getArgs().get(0));
            case IN -> criteriaBuilder.in(root.get(cb.getField())).value(cb.getArgs().get(0));
            case NOT_IN -> criteriaBuilder.in(root.get(cb.getField())).value(cb.getArgs().get(0)).not();
            case EMPTY_CONDITION -> criteriaBuilder.and();
            case LIKE_IGNORE_CASE ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(cb.getField())), (String) cb.getArgs().get(0));
            case NOT_CONTAIN -> criteriaBuilder.notLike(root.get(cb.getField()), (String) cb.getArgs().get(0));
            default -> throw new AppError("unknown query condition type");
        };
    }
}
