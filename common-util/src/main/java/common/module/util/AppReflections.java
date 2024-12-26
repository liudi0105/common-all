package common.module.util;

import common.module.util.errors.AppError;
import common.module.util.model.SerializableFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.reflections.Reflections;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppReflections {

    public static final String WRITE_REPLACE = "writeReplace";

    private static final Map<SerializableFunction<?, ?>, Field> fieldCache = new ConcurrentHashMap<>();
    private static final Map<SerializableFunction<?, ?>, String> fieldNameCache = new ConcurrentHashMap<>();

    public static <T, R> String getFieldName(SerializableFunction<T, R> function) {
        return fieldNameCache.computeIfAbsent(function, AppReflections::findFieldName);
    }

    public static <T> T forName(String name, List<Class<?>> clazz, List<Object> args) {
        return newInstance(clazz(name), clazz, args);
    }

    public static <T> T forName(String name) {
        return forName(name, List.of());
    }

    public static <T> T forName(String name, Object args) {
        return forName(name, List.of(args));
    }

    public static <T> T forName(String name, List<Object> args) {
        return newInstance(clazz(name), args);
    }

    public static Field getField(SerializableFunction<?, ?> function) {
        return fieldCache.computeIfAbsent(function, AppReflections::findField);
    }

    public static <E> Class<E> clazz(String className) {
        try {
            return (Class<E>) Class.forName(className);
        } catch (Exception e) {
            throw new AppError(e);
        }
    }

    public static <E> E newInstance(String className, List<Class<?>> clazz, List<Object> args) {
        try {
            return newInstance(clazz(className), clazz, args);
        } catch (Exception e) {
            throw new AppError(e);
        }
    }

    public static <E> E newInstance(Class<E> clazz, List<Class<?>> argCls, List<Object> args) {
        Class<?>[] array = argCls.toArray(Class[]::new);
        Object[] argsArr = args.toArray(Object[]::new);
        try {
            return clazz.getConstructor(array).newInstance(argsArr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <E> E newInstance(Class<E> clazz) {
        return newInstance(clazz, List.of());
    }

    public static <E> E newInstance(Class<E> clazz, List<Object> args) {
        try {
            List array = args.stream()
                    .peek(Objects::requireNonNull)
                    .map(Object::getClass).toList();
            return (E) newInstance(clazz, array, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <E, V> void setProperty(SerializableFunction<E, V> prop, E e, V value) {
        if (e == null) {
            return;
        }
        String fieldName = getFieldName(prop);
        setField(fieldName, e, value);
    }

    public static void setField(String fieldName, Object e, Object value) {
        try {
            Field declaredField = e.getClass().getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            declaredField.set(e, value);
        } catch (Exception ex) {
            throw new AppError(ex);
        }
    }

    public static String getMethodName(SerializableFunction<?, ?> function) {
        try {
            Method method = function.getClass().getDeclaredMethod(WRITE_REPLACE);
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(function);
            return serializedLambda.getImplMethodName();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new AppError("获取方法名失败");
        }
    }

    public static String findFieldName(SerializableFunction<?, ?> function) {
        try {
            String fieldName;
            // 第1步 获取SerializedLambda
            Method method = function.getClass().getDeclaredMethod(WRITE_REPLACE);
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(function);
            // 第2步 implMethodName 即为Field对应的Getter方法名
            String implMethodName = serializedLambda.getImplMethodName();
            if (implMethodName.startsWith("get") && implMethodName.length() > 3) {
                fieldName = Introspector.decapitalize(implMethodName.substring(3));
            } else if (implMethodName.startsWith("is") && implMethodName.length() > 2) {
                fieldName = Introspector.decapitalize(implMethodName.substring(2));
            } else if (implMethodName.startsWith("lambda$")) {
                throw new IllegalArgumentException("SerializableFunction不能传递lambda表达式,只能使用方法引用");
            } else {
                throw new IllegalArgumentException(implMethodName + "不是Getter方法引用");
            }
            return fieldName;
        } catch (Exception e) {
            throw new AppError("failed to parse field name");
        }
    }

    public static Field findField(SerializableFunction<?, ?> function) {
        Field field = null;
        String fieldName = null;
        try {
            // 第1步 获取SerializedLambda
            Method method = function.getClass().getDeclaredMethod(WRITE_REPLACE);
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(function);
            // 第2步 implMethodName 即为Field对应的Getter方法名
            String implMethodName = serializedLambda.getImplMethodName();
            if (implMethodName.startsWith("get") && implMethodName.length() > 3) {
                fieldName = Introspector.decapitalize(implMethodName.substring(3));
            } else if (implMethodName.startsWith("is") && implMethodName.length() > 2) {
                fieldName = Introspector.decapitalize(implMethodName.substring(2));
            } else if (implMethodName.startsWith("lambda$")) {
                throw new IllegalArgumentException("SerializableFunction不能传递lambda表达式,只能使用方法引用");

            } else {
                throw new IllegalArgumentException(implMethodName + "不是Getter方法引用");
            }
            // 第3步 获取的Class是字符串，并且包名是“/”分割，需要替换成“.”，才能获取到对应的Class对象
            String declaredClass = serializedLambda.getImplClass().replace("/", ".");
            Class<?> aClass = Class.forName(declaredClass, false, AppReflections.class.getClassLoader());

            // 第4步  Spring 中的反射工具类获取Class中定义的Field
            field = aClass.getDeclaredField(fieldName);

        } catch (Exception e) {
            throw new AppError("failed to find field");
        }
        // 第5步 如果没有找到对应的字段应该抛出异常
        if (field != null) {
            return field;
        }
        throw new NoSuchFieldError(fieldName);
    }

    public static <T extends Annotation> Map<Field, T> getFieldsAnnotatedWith(Class<?> typeClass, Class<T> annoClass) {
        return Arrays.stream(typeClass.getDeclaredFields())
                .map(v -> new Object[]{v, v.getAnnotation(annoClass)})
                .filter(v -> v[1] != null)
                .collect(Collectors.toMap(v -> (Field) v[0], v -> (T) v[1]));
    }

    public static <T extends Annotation> Map<String, T> getFieldNamesAnnotatedWith(Class<?> typeClass, Class<T> annoClass) {
        return getFieldsAnnotatedWith(typeClass, annoClass)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(v -> v.getKey().getName(), Map.Entry::getValue));
    }

    public static Set<Class<?>> getTypesAnnotatedWith(String packageName, Class<? extends Annotation> clazz) {
        return new Reflections(packageName).getTypesAnnotatedWith(clazz);
    }

    public static Set<Class<?>> getTypesAnnotatedWith(Class<?> packageClass, Class<? extends Annotation> clazz) {
        return new Reflections(packageClass.getPackage().getName()).getTypesAnnotatedWith(clazz);
    }

    public static Set<Class<?>> getSubTypeOf(String packageName, Class<?> clazz) {
        return new Reflections(packageName).getSubTypesOf((Class<Object>) clazz);
    }

    public static <T> Class<T> gaCls3(Class<?> subClass) {
        return nthGenericArgClass(subClass, 2);
    }

    public static <T> Class<T> gaCls2(Class<?> subClass) {
        return nthGenericArgClass(subClass, 1);
    }

    public static <T> Class<T> gaCls1(Class<?> subClass) {
        return nthGenericArgClass(subClass, 0);
    }

    public static <T> Class<T> nthGenericArgClass(Class<?> subClass, int index) {
        Type clazz = subClass.getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) clazz;
        return (Class<T>) parameterizedType.getActualTypeArguments()[index];
    }
}
