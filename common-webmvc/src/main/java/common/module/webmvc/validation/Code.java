package common.module.webmvc.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.*;
import java.util.regex.Pattern;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = Code.ConstValidation.class)
public @interface Code {

    String message() default "value is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


    @Slf4j
    class ConstValidation implements ConstraintValidator<Code, String> {
        private final Pattern pattern = Pattern.compile("^[_A-Z]+[_0-9A-Z]+");

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            boolean matches = pattern.matcher(value).matches();
            if (!matches) {
                log.error("Invalid code: {}", value);
            }
            return matches;
        }
    }
}
