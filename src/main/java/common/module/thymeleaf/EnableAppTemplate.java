package common.module.thymeleaf;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ThymeleafConfiguration.class})
public @interface EnableAppTemplate {
}
