package common.module.util;

import java.util.Arrays;
import java.util.Optional;

public class AppEnums {
    public static <T extends Enum<T>> Optional<T> parseName(T[] enums, String name) {
        return Arrays.stream(enums).filter(v -> v.name().equals(name)).findAny();
    }

    public static <T extends Enum<T>> T parse(T[] enums, String name) {
        return parseName(enums, name)
                .orElseThrow(() -> new RuntimeException("Enum not found: " + name));
    }
}
