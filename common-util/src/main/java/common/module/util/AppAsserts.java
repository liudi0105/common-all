package common.module.util;


import java.util.Objects;
import java.util.function.BooleanSupplier;

public class AppAsserts {
    private AppAsserts() {
    }

    public static <T> void assertEqual(T a, T b, String msg) {
        if (!Objects.equals(a, b)) {
            throw new RuntimeException(msg);
        }
    }

    public static void failFastDev(BooleanSupplier supplier, String msg) {
        if (supplier.getAsBoolean()) {
            throw new RuntimeException(msg);
        }
    }

}
