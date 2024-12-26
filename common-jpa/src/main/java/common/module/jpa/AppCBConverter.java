package common.module.jpa;

import common.module.jpa.condition.ConditionBuilder;
import common.module.jpa.condition.QueryBuilder;
import common.module.util.AppReflections;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class AppCBConverter {

    private static final Converter target = AppReflections.forName("common.module.jpa.condition.CBConverterAdapter");

    public static <E> Specification<E> toSpecification(ConditionBuilder<E> eConditionBuilder) {
        return target.toSpecification(eConditionBuilder);
    }

    public static <E> Specification<E> toSpecification(QueryBuilder<E> queryBuilder) {
        return target.toSpecification(queryBuilder);
    }

    public static <E> Predicate toPredicate(QueryBuilder<E> qb, Root<E> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return target.toPredicate(qb, root, criteriaQuery, criteriaBuilder);
    }

    public static <E> Predicate toPredicate(ConditionBuilder<E> cb, Root<E> root, CriteriaBuilder criteriaBuilder) {
        return target.toPredicate(cb, root, criteriaBuilder);
    }

    public interface Converter {

        <E> Specification<E> toSpecification(ConditionBuilder<E> conditionBuilder);

        <E> Specification<E> toSpecification(QueryBuilder<E> queryBuilder);

        <E> Predicate toPredicate(QueryBuilder<E> qb, Root<E> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder);

        <E> Predicate toPredicate(ConditionBuilder<E> cb, Root<E> root, CriteriaBuilder criteriaBuilder);
    }
}
