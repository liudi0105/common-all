package common.module.annotations;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 自定义注解，条件判断 Web 环境
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(ConditionalOnWeb.WebCondition.class)  // 使用自定义的条件类 WebCondition
public @interface ConditionalOnWeb {
    // 这里可以定义一些自定义参数，如 Web 环境的类型等


     class WebCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            // 你可以根据具体的条件来判断是否满足 Web 环境
            // 例如：判断当前环境是否是 Web 环境，或者检查某些 Web 相关的配置是否存在

            // 示例：根据某个环境变量或属性来判断 Web 环境
            String webEnv = context.getEnvironment().getProperty("web.environment");

            // 如果 `web.environment` 属性存在并且为 `true`，则符合条件
            return StringUtils.hasText(webEnv) && "true".equalsIgnoreCase(webEnv);
        }
    }
}
