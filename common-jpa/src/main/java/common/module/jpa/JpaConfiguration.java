package common.module.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@EntityScan
@ComponentScan
public class JpaConfiguration {
//    @Bean
//    @ConditionalOnWebApplication
//    public AuditorAware<String> jpaAuditor() {
//        return new AuditorAware<>() {
//            @Autowired
//            private CurrentUser currentUser;
//
//            @Override
//            public Optional<String> getCurrentAuditor() {
//                String auditor = "anonymous";
//                try {
//                    return Optional.ofNullable(currentUser)
//                            .map(CurrentUser::getEmail)
//                            .or(() -> Optional.of(auditor));
//                } catch (BeanCreationException e) {
//                    // ignored
//                    return Optional.of(auditor);
//                }
//            }
//        };
//    }
}
