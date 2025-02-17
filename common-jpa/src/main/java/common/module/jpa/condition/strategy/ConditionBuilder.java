package common.module.jpa.condition.strategy;

import common.module.util.AppReflections;
import common.module.util.model.SerializableFunction;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class ConditionBuilder<E> {

    private String field;  // 字段名称
    private Collection<?> values;  // 对应字段的查询值
    private QueryConditionStrategy<E> strategy; // 当前条件的策略
    /**
     * 构造 IN 条件
     */
    public <V> ConditionBuilder<E> in(SerializableFunction<E, V> function, Collection<V> values) {
        this.field = AppReflections.getFieldName(function); // 获取字段名
        this.values = values; // 将 Collection 转换为 List
        this.strategy = new InConditionStrategy<>(this.field, this.values);
        return this;
    }

    /**
     * 构造 NOT IN 条件
     */
    public <V> ConditionBuilder<E> notIn(SerializableFunction<E, V> function, Collection<V> values) {
        this.field = StringUtils.capitalize(AppReflections.getFieldName(function)); // 获取字段名
        this.values = values;
        this.strategy = new IsNotInConditionStrategy<>(this.field, this.values);
        return this;
    }

    /**
     * 构造等于条件，支持 List 类型的多个值
     */
    public <V> ConditionBuilder<E> eq(SerializableFunction<E, V> function, V value) {
        this.field = AppReflections.getFieldName(function); // 获取字段名
        this.values = List.of(value); // 将单个值包装成 List
        this.strategy = new EqualConditionStrategy<>(this.field, value);
        return this;
    }

    /**
     * 构造 NOT EQUAL 条件
     */
    public <V> ConditionBuilder<E> notEq(SerializableFunction<E, V> function, V value) {
        this.field = AppReflections.getFieldName(function); // 获取字段名
        this.values = List.of(value);
        this.strategy = new IsNotEqualConditionStrategy<>(this.field, value);
        return this;
    }

    /**
     * 构造 LIKE 条件
     */
    public <V> ConditionBuilder<E> like(SerializableFunction<E, V> function, String value) {
        this.field = AppReflections.getFieldName(function); // 获取字段名
        this.values = List.of(value); // 将 LIKE 字符串值包装成 List
        this.strategy = new LikeConditionStrategy<>(this.field, value);
        return this;
    }

    /**
     * 构造 LIKE 忽略大小写条件
     */
    public <V> ConditionBuilder<E> likeIgnoreCase(SerializableFunction<E, V> function, String value) {
        this.field = AppReflections.getFieldName(function); // 获取字段名
        this.values = List.of(value.toLowerCase()); // 将 LIKE 字符串值包装成 List
        this.strategy = new LikeIgnoreCaseConditionStrategy<>(this.field, value);
        return this;
    }

    /**
     * 构造 BETWEEN 条件，支持 List 类型，包含两个范围值
     */
    public <V extends Comparable<V>> ConditionBuilder<E> between(SerializableFunction<E, V> function, V start, V end) {
        this.field = AppReflections.getFieldName(function); // 获取字段名
        this.values = List.of(start, end); // 将 start 和 end 包装成 List
        this.strategy = new BetweenConditionStrategy<>(this.field, start, end);
        return this;
    }

    /**
     * 构造 AND 条件，接受多个条件并组合
     */
    public ConditionBuilder<E> and(ConditionBuilder<E>... conditions) {
        this.strategy = new AndConditionStrategy<>(Arrays.stream(conditions).map(ConditionBuilder::getStrategy).toList());
        return this;
    }

    /**
     * 构造 OR 条件，接受多个条件并组合
     */
    public ConditionBuilder<E> or(ConditionBuilder<E>... conditions) {
        this.strategy = new OrConditionStrategy<>(Arrays.stream(conditions).map(ConditionBuilder::getStrategy).toList());
        return this;
    }

    /**
     * 构造 IS NULL 条件
     */
    public <V> ConditionBuilder<E> isNull(SerializableFunction<E, V> function) {
        this.field = StringUtils.capitalize(AppReflections.getFieldName(function)); // 获取字段名
        this.strategy = new IsNullConditionStrategy<>(this.field);
        return this;
    }

    /**
     * 构造 IS NOT NULL 条件
     */
    public <V> ConditionBuilder<E> isNotNull(SerializableFunction<E, V> function) {
        this.field = StringUtils.capitalize(AppReflections.getFieldName(function)); // 获取字段名
        this.strategy = new IsNotNullConditionStrategy<>(this.field);
        return this;
    }

    /**
     * 构造 IS EMPTY 条件
     */
    public <V> ConditionBuilder<E> isEmpty(SerializableFunction<E, V> function) {
        this.field = StringUtils.capitalize(AppReflections.getFieldName(function)); // 获取字段名
        this.strategy = new IsEmptyConditionStrategy<>(this.field);
        return this;
    }

    /**
     * 构造 IS NOT EMPTY 条件
     */
    public <V> ConditionBuilder<E> isNotEmpty(SerializableFunction<E, V> function) {
        this.field = StringUtils.capitalize(AppReflections.getFieldName(function)); // 获取字段名
        this.strategy = new IsNotEmptyConditionStrategy<>(this.field);
        return this;
    }

    // 获取当前条件的策略
    public QueryConditionStrategy<E> getStrategies() {
        return this.strategy;
    }
}
