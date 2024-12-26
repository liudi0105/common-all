package common.module.jpa.uuid;

import org.hibernate.annotations.IdGeneratorType;
import org.hibernate.annotations.ValueGenerationType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@IdGeneratorType(JpaUuidGenerator.class)
@ValueGenerationType(generatedBy = JpaUuidGenerator.class)
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface JpaUuid {
}
