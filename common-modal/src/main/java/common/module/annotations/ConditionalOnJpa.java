package common.module.annotations;

import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(ConditionalOnJpa.JpaCondition.class)  // 使用自定义的条件类 JpaCondition
public @interface ConditionalOnJpa {

    // 这里可以定义一些自定义参数，如 JPA 环境的配置类型等

    class JpaCondition implements Condition {

        private static Boolean isJpaEnabled = null;  // 缓存结果，避免多次运行

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            // 如果已经缓存过，直接返回缓存结果
            if (isJpaEnabled != null) {
                return isJpaEnabled;
            }

            try {
                // 通过上下文查找 EntityManager Bean
                EntityManager entityManager = context.getBeanFactory().getBean(EntityManager.class);
                isJpaEnabled = (entityManager != null);  // 如果找到 EntityManager，JPA 启用
            } catch (Exception e) {
                // 如果没有找到 EntityManager，说明 JPA 没有启用
                isJpaEnabled = false;
            }

            return isJpaEnabled;
        }
    }
}
