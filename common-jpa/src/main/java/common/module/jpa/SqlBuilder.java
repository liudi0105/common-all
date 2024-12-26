package common.module.jpa;

import common.module.util.errors.AppError;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Predicate;

@Slf4j
public class SqlBuilder {
    private final StringBuilder sb = new StringBuilder();
    @Getter
    private final Map<String, Object> params = new HashMap<>();
    public static final String PARAM_HOLDER = "?";

    public SqlBuilder appendNotNull(String segment, Object... value) {
        return appendOn(segment, Objects::nonNull, value);
    }

    public SqlBuilder appendNotEmpty(String segment, String... value) {
        return appendOn(segment, StringUtils::isNotEmpty, value);
    }

    public static SqlBuilder of(String... value) {
        SqlBuilder sqlBuilder = new SqlBuilder();
        Arrays.stream(value).forEach(sqlBuilder::append);
        return sqlBuilder;
    }

    public SqlBuilder append(String segment) {
        segment = segment.trim() + " ";
        sb.append(segment);
        return this;
    }

    public SqlBuilder append(String segment, Object... params) {
        int paramCount = StringUtils.countMatches(segment, PARAM_HOLDER);
        if (params == null) {
            if (paramCount != 1) {
                throw new AppError("参数个数不匹配");
            }
            return this;
        }
        if (paramCount != params.length) {
            log.error("params length not match, \"{}\", {}", segment, params);
            throw new AppError("参数个数不匹配");
        }
        Iterator<Object> iterator = Arrays.stream(params).iterator();
        while (segment.contains(PARAM_HOLDER)) {
            Object next = iterator.next();
            String key = ":p" + this.params.size();
            segment = segment.replaceFirst("\\?", key);
            this.params.put(key.replaceFirst(":", ""), next);
        }
        append(segment);
        return this;
    }

    public SqlBuilder appendNotEmpty(String segment, Collection<?> value) {
        return appendOn(segment, CollectionUtils::isNotEmpty, value);
    }

    public SqlBuilder appendNotBlank(String segment, String... value) {
        return appendOn(segment, StringUtils::isNotBlank, value);
    }

    public SqlBuilder appendContainsNotBlank(String segment, String value) {
        return appendOn(segment, v -> StringUtils.isNotBlank(value), '%' + value + '%');
    }

    @SafeVarargs
    public final <T> SqlBuilder appendOn(String segment, Boolean func, T... value) {
        if (func) {
            append(segment, (Object[]) value);
        }
        return this;
    }

    @SafeVarargs
    public final <T> SqlBuilder appendOn(String segment, Predicate<T> func, T... value) {
        return appendOn(segment, Arrays.stream(value).allMatch(func), value);
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
