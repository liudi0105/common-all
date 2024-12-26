package common.module.jpa;

import common.module.util.AppReflections;
import common.module.util.model.SerializableFunction;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Accessors(chain = true)
public class QueryBuilder<E> {

    private final ConditionBuilder<E> conditionBuilder = new ConditionBuilder<>();

    @Getter
    private ConditionBuilder<E> where = new ConditionBuilder<>();
    @Getter
    private ConditionBuilder<E> having;
    @Getter
    private Boolean distinct;
    @Getter
    private String[] orderBy;
    @Getter
    private Boolean asc = false;
    @Getter
    private String[] select;
    @Getter
    private Integer offset;

    public QueryBuilder() {
    }

    public Specification<E> toSpecification() {
        return AppCBConverter.toSpecification(this);
    }

    QueryBuilder<E> where(ConditionBuilder<E> func) {
        this.where = func;
        return this;
    }

    public QueryBuilder<E> where(UnaryOperator<ConditionBuilder<E>> func) {
        this.where = func.apply(this.where);
        return this;
    }

    @SafeVarargs
    public static <E> ConditionBuilder<E> or(ConditionBuilder<E>... conditionBuilders) {
        List<ConditionBuilder<E>> collect = Arrays
                .stream(conditionBuilders)
                .filter(v -> !v.getType().equals(ConditionBuilder.Type.EMPTY_CONDITION))
                .collect(Collectors.toList());

        ConditionBuilder<E> conditionBuilder = new ConditionBuilder<E>()
                .setType(ConditionBuilder.Type.OR);

        if (collect.isEmpty()) {
            return new ConditionBuilder<>();
        } else if (collect.size() == 1) {
            return collect.get(0);
        }
        conditionBuilder.setOrCondition(collect.toArray(new ConditionBuilder[0]));
        return conditionBuilder;
    }

    @SafeVarargs
    public static <E> ConditionBuilder<E> and(ConditionBuilder<E>... conditionBuilders) {
        List<ConditionBuilder<E>> collect = Arrays
                .stream(conditionBuilders)
                .filter(v -> !v.getType().equals(ConditionBuilder.Type.EMPTY_CONDITION))
                .collect(Collectors.toList());

        ConditionBuilder<E> conditionBuilder = new ConditionBuilder<E>()
                .setType(ConditionBuilder.Type.AND);

        if (collect.isEmpty()) {
            return new ConditionBuilder<>();
        } else if (collect.size() == 1) {
            return collect.get(0);
        }
        conditionBuilder.setAndCondition(collect.toArray(new ConditionBuilder[0]));
        return conditionBuilder;
    }

    public QueryBuilder<E> distinct() {
        distinct = true;
        return this;
    }

    @SafeVarargs
    public final QueryBuilder<E> select(SerializableFunction<E, ?>... functions) {
        select = Arrays.stream(functions).map(AppReflections::getFieldName).toArray(String[]::new);
        return this;
    }

    @SafeVarargs
    public final QueryBuilder<E> orderBy(SerializableFunction<E, ? extends Comparable<?>>... functions) {
        orderBy = Arrays.stream(functions).map(AppReflections::getFieldName).toArray(String[]::new);
        return this;
    }

    public final QueryBuilder<E> orderBy(String... fields) {
        if (ArrayUtils.isEmpty(fields)) {
            return this;
        }
        fields = Arrays
                .stream(fields)
                .filter(Objects::nonNull)
                .toArray(String[]::new);
        orderBy = fields;
        return this;
    }

    public QueryBuilder<E> asc(Boolean desc) {
        if (asc != null) {
            this.asc = desc;
        }
        return this;
    }

    public QueryBuilder<E> desc() {
        asc = false;
        return this;
    }

    public QueryBuilder<E> asc() {
        asc = true;
        return this;
    }

    public QueryBuilder<E> having(UnaryOperator<ConditionBuilder<E>> condition) {
        having = condition.apply(conditionBuilder);
        return this;
    }

    public QueryBuilder<E> having(ConditionBuilder<E> cs) {
        having = cs;
        return this;
    }

    public QueryBuilder<E> offset(int offset) {
        this.offset = offset;
        return this;
    }

}
