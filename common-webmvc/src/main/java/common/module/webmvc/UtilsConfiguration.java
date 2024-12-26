package common.module.webmvc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@ComponentScan
@Configuration
public class UtilsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AppWebClient appWebClient(RestTemplate restTemplate) {
        return new AppWebClient(restTemplate);
    }

}
