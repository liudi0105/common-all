package common.module.jpa;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
public @interface JpaRepo {
    Class<? extends BaseJpaEntity> entity();

    Class<? extends BaseJpaEntity>[] extra() default {};
}
