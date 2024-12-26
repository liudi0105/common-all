package common.module.webmvc;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.function.Consumer;

public class AppBootstrap {

    public static ConfigurableApplicationContext start(Class<?> clazz) {
        return start(clazz, springApplication -> {
        });
    }

    public static ConfigurableApplicationContext start(Class<?> clazz, Consumer<SpringApplication> consumer) {
        SpringApplication app = new SpringApplication(clazz);
        app.setAllowBeanDefinitionOverriding(false);
        app.setAllowCircularReferences(true);
        app.setBannerMode(Banner.Mode.OFF);
        app.setAdditionalProfiles("default-props");
        consumer.accept(app);
        return app.run();
    }

}
