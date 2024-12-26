package common.module.jpa;

import common.module.util.errors.AppError;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Predicate;

@Slf4j
public class Sql {
    private final StringBuilder sb = new StringBuilder();
    @Getter
    private final Map<String, Object> params = new HashMap<>();
    public static final String PARAM_HOLDER = "?";

    public Sql nonNull(String segment, Object... value) {
        return appendIfTrue(segment, Objects::nonNull, value);
    }

    public Sql nonEmpty(String segment, String... value) {
        return appendIfTrue(segment, StringUtils::isNotEmpty, value);
    }

    public static Sql of(String... value) {
        Sql sqlBuilder = new Sql();
        Arrays.stream(value).forEach(sqlBuilder::append);
        return sqlBuilder;
    }

    public Sql append(String segment) {
        segment = segment.trim() + " ";
        sb.append(segment);
        return this;
    }

    public Sql appendValue(String segment, Object... value) {
        int paramCount = StringUtils.countMatches(segment, PARAM_HOLDER);
        if (value == null) {
            if (paramCount != 1) {
                throw new AppError("参数个数不匹配");
            }
            return this;
        }
        if (paramCount != value.length) {
            log.error("params length not match, \"{}\", {}", segment, value);
            throw new AppError("参数个数不匹配");
        }
        Iterator<Object> iterator = Arrays.stream(value).iterator();
        while (segment.contains(PARAM_HOLDER)) {
            Object next = iterator.next();
            String key = ":p" + params.size();
            segment = segment.replaceFirst("\\?", key);
            params.put(key.replaceFirst(":", ""), next);
        }
        append(segment);
        return this;
    }

    public Sql notEmpty(String segment, Collection<?> value) {
        return appendIfTrue(segment, CollectionUtils::isNotEmpty, value);
    }

    public Sql nonBlank(String segment, String... value) {
        return appendIfTrue(segment, StringUtils::isNotBlank, value);
    }

    public Sql nonBlankLike(String segment, String value) {
        if (StringUtils.isBlank(value)) {
            return this;
        }
        return appendValue(segment, "%" + value + "%");
    }

    @SafeVarargs
    public final <T> Sql appendIfTrue(String segment, Boolean func, T... value) {
        if (func) {
            appendValue(segment, value);
        }
        return this;
    }

    @SafeVarargs
    public final <T> Sql appendIfTrue(String segment, Predicate<T> func, T... value) {
         return appendIfTrue(segment, Arrays.stream(value).allMatch(func), value);
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
