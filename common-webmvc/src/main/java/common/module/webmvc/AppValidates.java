package common.module.webmvc;

import common.module.util.errors.AppWarning;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Set;

public class AppValidates {

    @Getter
    @Accessors(fluent = true)
    private static final Validator VALIDATOR;

    static {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.afterPropertiesSet();
        VALIDATOR = localValidatorFactoryBean;
    }

    public static void validate(Object validate) {
        Set<ConstraintViolation<Object>> constraintViolations = VALIDATOR.validate(validate);
        if (!constraintViolations.isEmpty()) {
            throw new AppWarning(constraintViolations.toString());
        }
    }

    public static void nonNull(Object object, String message) {
        if (object == null) {
            throw new AppWarning(" is null", message);
        }
    }
}
