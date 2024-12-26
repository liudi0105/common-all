package common.module.webmvc;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping
@ResponseBody
@Operation
@Inherited
public @interface Api {

    @AliasFor(annotation = RequestMapping.class)
    String[] value() default {};

    @AliasFor(annotation = RequestMapping.class)
    String[] path() default {};

    boolean log() default false;

    @AliasFor(attribute = "summary", annotation = Operation.class)
    String description() default "";

    @AliasFor(annotation = RequestMapping.class)
    RequestMethod[] method() default {RequestMethod.POST};

    @AliasFor(annotation = RequestMapping.class)
    String[] consumes() default {};

    String hasRole() default "";
}
