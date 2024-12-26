package common.module.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public class AppNumbers {
    public AppNumbers() {
    }

    public static String toStr(BigDecimal bigDecimal) {
        return bigDecimal != null ? DecimalFormat.getInstance().format(bigDecimal.doubleValue()) : "null";
    }

    public static <T> BigDecimal sumOr0(Collection<T> collection, Function<T, BigDecimal> func) {
        return collection.stream().map(func).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public static <T> BigDecimal sumOrDefault(Collection<T> collection, Function<T, BigDecimal> func, BigDecimal bigDecimal) {
        return collection.stream().map(func).reduce(BigDecimal::add).orElse(bigDecimal);
    }

    public static <T> Optional<BigDecimal> sum(Collection<T> collection, Function<T, BigDecimal> func) {
        return collection.stream().map(func).reduce(BigDecimal::add);
    }
}
