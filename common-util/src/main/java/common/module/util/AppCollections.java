package common.module.util;

import com.google.common.collect.Lists;
import common.module.util.model.SerializableFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppCollections {

    public static <T> T last(T[] arr) {
        return last(Lists.newArrayList(arr));
    }

    @SafeVarargs
    public static <T> List<T> concat(Collection<T>... c) {
        List<T> l = Lists.newArrayList();
        if (c != null) {
            for (Collection<T> ts : c) {
                l.addAll(ts);
            }
        }
        return l;
    }

    public static <T> T last(List<T> arr) {
        if (arr == null || arr.isEmpty()) {
            return null;
        }
        return arr.get(arr.size() - 1);
    }

    public static <T> Map<String, T> toMap(Collection<T> collection, Function<T, String> string) {
        return collection.stream().collect(Collectors.toMap(string, Function.identity()));
    }

    // return elements in the first collection and not in the second
    public static <T, V> List<T> onlyFirst(Collection<T> first, Collection<T> second, SerializableFunction<T, V> func) {
        Set<V> collect = second.stream().map(func).collect(Collectors.toSet());
        return first.stream()
                .filter(v -> collect.contains(func.apply(v)))
                .collect(Collectors.toList());
    }

    public static Set<String> toSet(String[] arr) {
        return Arrays.stream(arr).collect(Collectors.toSet());
    }

    public static <T> List<T> toList(Iterable<T> arr) {
        List<T> list = Lists.newArrayList();
        arr.forEach(list::add);
        return list.stream().collect(Collectors.toList());
    }

    public static <T> Stream<T> toStream(Iterable<T> arr) {
        List<T> list = Lists.newArrayList();
        arr.forEach(list::add);
        return list.stream();
    }

    public static List<String> toList(String[] arr) {
        return Arrays.stream(arr).collect(Collectors.toList());
    }

    @SafeVarargs
    public static <T> List<T> list(Collection<T>... elements) {
        List<T> objects = Lists.newArrayList();
        add(objects, elements);
        return objects;
    }

    @SafeVarargs
    public static <T> void add(Collection<T> to, Collection<T>... elements) {
        if (elements == null) {
            return;
        }
        for (var e : elements) {
            if (CollectionUtils.isNotEmpty(e)) {
                add(to, e);
            }
        }
    }

    public static <T> void add(Collection<T> to, Collection<T> elements) {
        if (CollectionUtils.isNotEmpty(elements)) {
            for (T e : elements) {
                if (e != null) {
                    to.addAll(elements);
                }
            }
        }
    }

    @SafeVarargs
    public static <T> List<T> filterBlank(T... values) {
        return Arrays
                .stream(values)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static <T> List<T> onlyFirst(Collection<T> first, Collection<T> second) {
        return onlyFirst(first, second, v -> v);
    }

}
