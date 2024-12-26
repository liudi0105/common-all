package common.module.dto;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValueWrapper<T> {
    private T value;

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Constraint(validatedBy = NotBlank.NotBlankValidation.class)
    public @interface NotBlank {
        String message() default "value in wrapper is blank";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

        class NotBlankValidation implements ConstraintValidator<NotBlank, ValueWrapper<String>> {
            @Override
            public boolean isValid(ValueWrapper<String> value, ConstraintValidatorContext context) {
                return value != null && StringUtils.isNotBlank(value.value);
            }
        }
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Constraint(validatedBy = NotNull.NotNullValidation.class)
    public @interface NotNull {
        String message() default "value is null";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

        class NotNullValidation implements ConstraintValidator<NotNull, ValueWrapper<?>> {
            @Override
            public boolean isValid(ValueWrapper<?> value, ConstraintValidatorContext context) {
                return value != null && value.value != null;
            }
        }
    }

}
