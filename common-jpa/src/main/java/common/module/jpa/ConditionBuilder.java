package common.module.jpa;

import common.module.util.AppJsons;
import common.module.util.AppReflections;
import common.module.util.model.SerializableFunction;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ConditionBuilder<E> {

    private String field;
    private List<?> args;
    private String alias;

    public ConditionBuilder<E>[] orCondition;
    public ConditionBuilder<E>[] andCondition;

    private Type type;

    public ConditionBuilder() {
        this.type = Type.EMPTY_CONDITION;
    }

    public Specification<E> toSpecification() {
        return AppCBConverter.toSpecification(this);
    }

    public static <E> ConditionBuilder<E> fromDTO(Object param, Class<E> clazz) {
        Set<String> collect = Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());

        ConditionBuilder<E> c = new ConditionBuilder<>();
        if (param == null) {
            return c;
        }
        Set<Map.Entry<String, Object>> entries = AppJsons.toMap(param).entrySet()
                .stream().filter(v -> collect.contains(v.getKey())).collect(Collectors.toSet());
        for (var e : entries) {
            String k = e.getKey();
            Object v = e.getValue();
            if (v instanceof String) {
                String s = (String) v;
                c = c.contains(k, s);
            } else if (v instanceof Boolean || v instanceof Number) {
                c = c.eq(k, v);
            }
        }

        return c;
    }

    public QueryBuilder<E> toQB() {
        return new QueryBuilder<E>().where(this);
    }

    private ConditionBuilder<E> combine(ConditionBuilder<E> cbs) {
        if (Type.EMPTY_CONDITION.equals(cbs.getType())) {
            return this;
        }
        if (Type.EMPTY_CONDITION.equals(this.getType())) {
            return cbs;
        }
        return QueryBuilder.and(this, cbs);
    }

    private ConditionBuilder<E> ofField(Type type, SerializableFunction<E, ?> field) {
        return ofField(type, AppReflections.getFieldName(field));
    }

    private ConditionBuilder<E> ofField(Type type, String field) {
        return ConditionBuilder.<E>builder()
                .type(type)
                .field(field)
                .build();
    }

    private <V> ConditionBuilder<E> ofTuple3(Type type, SerializableFunction<E, V> function, V v1, V v2) {
        return ofTuple3(type, AppReflections.getFieldName(function), v1, v2);
    }

    private ConditionBuilder<E> ofTuple3(Type type, String field, Object arg1, Object arg2) {
        return ConditionBuilder.<E>builder()
                .type(type)
                .field(field)
                .args(List.of(arg1, arg2))
                .build();
    }

    private ConditionBuilder<E> ofTuple2(Type type, SerializableFunction<E, ?> function, Object value) {
        return ofTuple2(type, AppReflections.getFieldName(function), value);
    }

    private ConditionBuilder<E> ofTuple2(Type type, String field, Object value) {
        return ConditionBuilder.<E>builder()
                .type(type)
                .field(field)
                .args(List.of(value))
                .build();
    }

    public <V> ConditionBuilder<E> in(SerializableFunction<E, V> function, Collection<V> value) {
        return in(AppReflections.getFieldName(function), value);
    }

    public ConditionBuilder<E> in(String function, Collection<?> value) {
        if (value == null) {
            return this;
        }
        return combine(ofTuple2(Type.IN, function, value));
    }

    public <V> ConditionBuilder<E> notIn(SerializableFunction<E, V> function, Collection<V> value) {
        if (value == null) return this;
        return combine(ofTuple2(Type.NOT_IN, function, value));
    }

    public <V> ConditionBuilder<E> inNotEmpty(SerializableFunction<E, V> function, Collection<V> value) {
        return inNotEmpty(AppReflections.getFieldName(function), value);
    }

    public ConditionBuilder<E> inNotEmpty(String function, Collection<?> value) {
        if (CollectionUtils.isEmpty(value)) {
            return notEmpty(function);
        }
        return combine(ofTuple2(Type.IN, function, value));
    }

    private ConditionBuilder<E> notEmpty(String field) {
        ConditionBuilder<E> isNull = combine(ofField(Type.IS_NULL, field));
        ConditionBuilder<E> isNotNull = combine(ofField(Type.NOT_NULL, field));
        return QueryBuilder.and(isNull, isNotNull);
    }

    public ConditionBuilder<E> eq(String function, Object value) {
        if (value == null) return this;
        return combine(ofTuple2(Type.EQUAL, function, value));
    }


    public <V> ConditionBuilder<E> eq(SerializableFunction<E, V> function, V value) {
        if (value == null) return this;
        return eq(AppReflections.getFieldName(function), value);
    }

    public ConditionBuilder<E> notEq(String function, Object value) {
        if (value == null) return this;
        return combine(ofTuple2(Type.NOT_EQUAL, function, value));
    }

    public <V> ConditionBuilder<E> notEq(SerializableFunction<E, V> function, V value) {
        if (value == null) return this;
        return notEq(AppReflections.getFieldName(function), value);
    }

    @SafeVarargs
    public final ConditionBuilder<E> or(ConditionBuilder<E>... c) {
        return QueryBuilder.and(this, QueryBuilder.or(c));
    }

    @SafeVarargs
    public final ConditionBuilder<E> or(Function<ConditionBuilder<E>, ConditionBuilder<E>>... func) {
        ConditionBuilder[] conditionBuilders = new ConditionBuilder[func.length];
        for (int i = 0; i < conditionBuilders.length; i++) {
            conditionBuilders[i] = func[i].apply(new ConditionBuilder<>());
        }
        return or(conditionBuilders);
    }

    public ConditionBuilder<E> startsWith(SerializableFunction<E, String> function, String value) {
        if (StringUtils.isBlank(value)) return this;
        return combine(ofTuple2(Type.LIKE, function, value + "%"));
    }

    public ConditionBuilder<E> endsWith(SerializableFunction<E, String> function, String value) {
        if (StringUtils.isBlank(value)) return this;
        return combine(ofTuple2(Type.LIKE, function, "%" + value));
    }

    public ConditionBuilder<E> contains(SerializableFunction<E, String> function, String value) {
        if (StringUtils.isBlank(value)) return this;
        return combine(ofTuple2(Type.LIKE, function, "%" + value + "%"));
    }

    public ConditionBuilder<E> notContain(SerializableFunction<E, String> function, String value) {
        if (StringUtils.isBlank(value)) return this;
        return combine(ofTuple2(Type.NOT_CONTAIN, function, value));
    }

    public ConditionBuilder<E> isNull(SerializableFunction<E, ?> function) {
        return combine(ofField(Type.IS_NULL, function));
    }

    public ConditionBuilder<E> notNull(SerializableFunction<E, ?> function) {
        return combine(ofField(Type.NOT_NULL, function));
    }


    public ConditionBuilder<E> isEmpty(SerializableFunction<E, String> function) {
        return combine(ofField(Type.IS_EMPTY, function));
    }


    public ConditionBuilder<E> notEmpty(SerializableFunction<E, String> function) {
        return combine(ofField(Type.NOT_EMPTY, function));
    }

    public ConditionBuilder<E> contains(String function, String value) {
        if (StringUtils.isBlank(value)) return this;
        return combine(ofTuple2(Type.LIKE, function, "%" + value.trim() + "%"));
    }

    public ConditionBuilder<E> likeIgnoreCase(SerializableFunction<E, String> function, String value) {
        if (StringUtils.isBlank(value)) {
            return this;
        }
        return combine(ofTuple2(Type.LIKE_IGNORE_CASE, function, "%" + value.trim().toLowerCase() + "%"));
    }

    public <V extends Comparable<V>> ConditionBuilder<E> before(SerializableFunction<E, V> function, V value) {
        if (value == null) return this;
        return lt(function, value);
    }

    public <V extends Comparable<V>> ConditionBuilder<E> after(SerializableFunction<E, V> function, V value) {
        if (value == null) return this;
        return gt(function, value);
    }

    public <V extends Comparable<V>> ConditionBuilder<E> lt(SerializableFunction<E, V> function, V value) {
        if (value == null) return this;
        return combine(ofTuple2(Type.LESS_THAN, function, value));
    }

    public <V extends Comparable<V>> ConditionBuilder<E> lte(SerializableFunction<E, V> function, V value) {
        if (value == null) return this;
        return combine(ofTuple2(Type.LESS_EQUAL, function, value));
    }

    public <V extends Comparable<V>> ConditionBuilder<E> gt(SerializableFunction<E, V> function, V value) {
        if (value == null) return this;
        return combine(ofTuple2(Type.GREATER_THAN, function, value));
    }

    public <V extends Comparable<V>> ConditionBuilder<E> gte(SerializableFunction<E, V> function, V value) {
        if (value == null) return this;
        return combine(ofTuple2(Type.GREATER_EQUAL, function, value));
    }

    public <V extends Comparable<V>> ConditionBuilder<E> between(SerializableFunction<E, V> function, V start, V end) {
        if (ObjectUtils.anyNull(start, end)) return this;
        return combine(ofTuple3(Type.BETWEEN, function, start, end));
    }

    @Override
    public String toString() {
        return "ConditionBuilder{" +
                "field='" + field + '\'' +
                ", args=" + args +
                ", alias='" + alias + '\'' +
                ", orCondition=" + Arrays.toString(orCondition) +
                ", andCondition=" + Arrays.toString(andCondition) +
                ", type=" + type +
                '}';
    }

    public enum Type {
        NON_EXIST,
        EMPTY_CONDITION,
        EQUAL, NOT_EQUAL, LIKE, IN, NOT_IN,
        IS_NULL, NOT_NULL, IS_EMPTY, NOT_EMPTY,
        OR, AND,
        BEFORE, AFTER, NOT_CONTAIN,
        GREATER_THAN, LESS_THAN, LESS_EQUAL, GREATER_EQUAL, BETWEEN,
        LIKE_IGNORE_CASE
    }
}
