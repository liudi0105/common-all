package common.module.util;

import common.module.util.model.SerializableFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppBeans {

    /**
     * 获取对象中字段值为 null 或非 null 的属性名称集合。
     *
     * @param source   源对象
     * @param nullable true 表示获取值为 null 的属性；false 表示获取值非 null 的属性
     * @return 属性名称集合
     */
    private static Set<String> nullablePropSet(Object source, boolean nullable) {
        if (source == null) {
            throw new IllegalArgumentException("Source object cannot be null");
        }

        try {
            return Arrays.stream(PropertyUtils.getPropertyDescriptors(source))
                    .map(descriptor -> descriptor.getName())
                    .filter(name -> {
                        try {
                            Object value = PropertyUtils.getProperty(source, name);
                            return (value == null) == nullable;
                        } catch (Exception e) {
                            return false; // Skip inaccessible properties
                        }
                    })
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve properties", e);
        }
    }

    /**
     * 拷贝属性值。
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        try {
            PropertyUtils.copyProperties(target, source);
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy properties", e);
        }
    }

    /**
     * 获取对象指定属性的值。
     *
     * @param obj      对象
     * @param propName 属性名称
     * @param <V>      属性类型
     * @return 属性值
     */
    public static <V> V valueOf(Object obj, String propName) {
        try {
            return (V) PropertyUtils.getProperty(obj, propName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get property value: " + propName, e);
        }
    }

    /**
     * 获取对象指定属性的值（通过函数引用）。
     *
     * @param obj  对象
     * @param func 属性函数引用
     * @param <T>  对象类型
     * @param <V>  属性类型
     * @return 属性值
     */
    public static <T, V> V valueOf(T obj, SerializableFunction<T, V> func) {
        String fieldName = AppReflections.getFieldName(func);
        return valueOf(obj, fieldName);
    }

    /**
     * 拷贝非 null 的字段值。
     *
     * @param source 源对象
     * @param target 目标对象
     * @param <T>    目标对象类型
     * @return 目标对象
     */
    public static <T> T copyNonNullField(Object source, T target) {
        try {
            String[] nullProps = nullPropArray(source);
            PropertyUtils.copyProperties(target, source);

            // 重置 null 属性
            for (String nullProp : nullProps) {
                PropertyUtils.setProperty(target, nullProp, null);
            }
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy non-null fields", e);
        }
    }

    /**
     * 获取对象中值为 null 的属性名称集合。
     *
     * @param source 源对象
     * @return 值为 null 的属性名称集合
     */
    public static Set<String> nullProps(Object source) {
        return nullablePropSet(source, true);
    }

    /**
     * 获取对象中值非 null 的属性名称集合。
     *
     * @param source 源对象
     * @return 值非 null 的属性名称集合
     */
    public static Set<String> nonNullProps(Object source) {
        return nullablePropSet(source, false);
    }

    /**
     * 获取对象中值为 null 的属性名称数组。
     *
     * @param source 源对象
     * @return 值为 null 的属性名称数组
     */
    public static String[] nullPropArray(Object source) {
        return nullProps(source).toArray(new String[0]);
    }

    /**
     * 获取对象中值非 null 的属性名称数组。
     *
     * @param source 源对象
     * @return 值非 null 的属性名称数组
     */
    public static String[] nonNullPropArray(Object source) {
        return nonNullProps(source).toArray(new String[0]);
    }

    /**
     * 将源对象转换为指定类型的对象，仅拷贝非 null 字段。
     *
     * @param o     源对象
     * @param clazz 目标对象类型
     * @param <T>   目标对象类型
     * @return 转换后的目标对象
     */
    public static <T> T convert(Object o, Class<T> clazz) {
        T t = AppReflections.newInstance(clazz);
        copyNonNullField(o, t);
        return t;
    }

    /**
     * 将对象集合转换为指定类型的集合，仅拷贝非 null 字段。
     *
     * @param collection 对象集合
     * @param clazz      目标对象类型
     * @param <T>        目标对象类型
     * @return 转换后的集合
     */
    public static <T> List<T> convertList(Collection<?> collection, Class<T> clazz) {
        return collection.stream()
                .map(v -> convert(v, clazz))
                .collect(Collectors.toList());
    }
}