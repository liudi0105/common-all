package common.module.jpa.condition.query;

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
@Accessors(chain = true)
public class ConditionBuilder<E> {

    private String field;  // 字段名称
    private Collection<?> values;  // 对应字段的查询值
    private QueryConditionStrategy<E> strategies; // 当前条件的策略

    public <V> ConditionBuilder<E> in(SerializableFunction<E, V> function, Collection<V> value) {
        this.field = StringUtils.capitalize(AppReflections.getFieldName(function)); // 获取字段名
        this.values = value;
        this.strategies = new InConditionStrategy<>(this.field, value);
        return this;
    }

    /**
     * 构造等于条件
     */
    public <V> ConditionBuilder<E> eq(SerializableFunction<E, V> function, V value) {
        this.field = StringUtils.capitalize(AppReflections.getFieldName(function)); // 获取字段名
        this.values = List.of(value);
        this.strategies = new EqualConditionStrategy<>(this.field, value);
        return this;
    }

    /**
     * 构造 LIKE 条件
     */
    public <V> ConditionBuilder<E> like(SerializableFunction<E, V> function, String value) {
        this.field = StringUtils.capitalize(AppReflections.getFieldName(function)); // 获取字段名
        this.values = List.of(value);
        this.strategies = new LikeConditionStrategy<>(this.field, value);
        return this;
    }

    /**
     * 构造 BETWEEN 条件
     */
    public <V extends Comparable<V>> ConditionBuilder<E> between(SerializableFunction<E, V> function, V start, V end) {
        this.field = StringUtils.capitalize(AppReflections.getFieldName(function)); // 获取字段名
        this.values = List.of(start, end);
        this.strategies = new BetweenConditionStrategy<>(this.field, start, end);
        return this;
    }

    /**
     * 构造 AND 条件，接受多个条件并组合
     */
    public ConditionBuilder<E> and(ConditionBuilder<E>... conditions) {
        this.strategies = new AndConditionStrategy<>(Arrays.stream(conditions).map(ConditionBuilder::getStrategies).toList());
        return this;
    }

    /**
     * 构造 OR 条件，接受多个条件并组合
     */
    public ConditionBuilder<E> or(ConditionBuilder<E>... conditions) {
        this.strategies = new OrConditionStrategy<>(Arrays.stream(conditions).map(ConditionBuilder::getStrategies).toList());
        return this;
    }

    // 获取当前条件的策略
    public QueryConditionStrategy<E> getStrategies() {
        return this.strategies;
    }
}
