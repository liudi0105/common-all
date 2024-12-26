package common.module.webmvc;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({WebMvcConfiguration.class,  UtilsConfiguration.class})
@Configuration
@ComponentScan
@EnableAsync
public @interface EnableAppWeb {
}
