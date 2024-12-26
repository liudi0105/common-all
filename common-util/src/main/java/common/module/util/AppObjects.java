package common.module.util;


import common.module.util.errors.AppError;

import java.util.function.Consumer;

public class AppObjects {
    public static <T> T or(T t, T r) {
        return t == null ? r : t;
    }

    public static void nonNull(Object object, String msg, Object... args) {
        if (object == null) {
            throw new AppError(msg, args);
        }
    }

    public static <T> void onNonNUll(T value, Consumer<T> t) {
        if (value != null) {
            t.accept(value);
        }
    }
}
