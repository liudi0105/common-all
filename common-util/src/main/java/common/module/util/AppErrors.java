package common.module.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppErrors {

    public static <T extends Throwable> Optional<T> findCause(Exception e, Class<T> targetClass) {
        Throwable throwable = e;
        while ((throwable = throwable.getCause()) != null) {
            if (targetClass.isAssignableFrom(throwable.getClass())) {
                return Optional.of((T) throwable);
            }
        }
        return Optional.empty();
    }

    public static String stackTraceString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

}
