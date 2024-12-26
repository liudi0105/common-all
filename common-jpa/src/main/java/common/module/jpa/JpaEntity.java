package common.module.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Entity
@Table
@MappedSuperclass
public @interface JpaEntity {
    @AliasFor(annotation = Table.class)
    String name() default "";
}
