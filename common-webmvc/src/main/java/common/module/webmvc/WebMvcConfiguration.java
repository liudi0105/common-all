package common.module.webmvc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import common.module.util.errors.AppError;
import common.module.util.AppJsons;
import common.module.util.JsonSerializers;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.LayeredConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.CacheControl;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;

@ComponentScan
public class WebMvcConfiguration {

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/static/**")
                        .addResourceLocations("classpath:/static/")
                        .setCacheControl(CacheControl.noStore());
            }
        };
    }

    @Autowired
    private AppSprings appSprings;

    @Bean
    @ConditionalOnMissingBean
    public AppCookies appCookieUtils() {
        return new AppCookies();
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return AppWebClient.newRestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean
    public AppWebRequest appWebRequest() {
        return new AppWebRequest();
    }

//    @Bean
//    @RequestScope
//    @ConditionalOnMissingBean
//    public CurrentUser currentUser() {
//        return new CurrentUser();
//    }

    @Bean
    public AppExceptionHandler appExceptionHandler() {
        return new AppExceptionHandler();
    }

    // 解决RequestScope问题
//    @Bean
//    public RequestContextListener requestContextListener() {
//        return new RequestContextListener();
//    }

//    @Bean
//    public RequestContextFilter requestContextFilter() {
//        return new RequestContextFilter();
//    }

    public static HttpComponentsClientHttpRequestFactory unsafeRequestFactory() {
        // https 访问解决
        TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts
                    .custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
        assert sslContext != null;
        LayeredConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        // 注册 http、https 对应连接工厂
        Registry<ConnectionSocketFactory> sfr = RegistryBuilder
                .<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", csf)
                .build();
        // 连接池配置
        PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(sfr);
        // 最大连接数
        pollingConnectionManager.setMaxTotal(50);
        // 单路由的并发数
        pollingConnectionManager.setDefaultMaxPerRoute(50);
        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setConnectionManager(pollingConnectionManager)
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        //获取连接超时 为毫秒为单位
        factory.setConnectTimeout(5000);
        //请求超时 为毫秒为单位
        factory.setConnectionRequestTimeout(10000);
        return factory;
    }

    public static SSLContext unsafeSslContext() {
        TrustManager[] byPassTrustManagers = new TrustManager[]{
                new X509TrustManager() {

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }
                }};

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            throw new AppError(e);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = AppJsons.newObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        return objectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return jsonConverter();
    }

    public static MappingJackson2HttpMessageConverter jsonConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addSerializer(LocalDateTime.class, new JsonSerializers.LocalDateTimeSerializer());
        timeModule.addDeserializer(LocalDateTime.class, new JsonSerializers.LocalDateTimeDeserializer());

        timeModule.addSerializer(Instant.class, new JsonSerializers.InstantSerializer());
        timeModule.addDeserializer(Instant.class, new JsonSerializers.InstantDeserializer());

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(BigDecimal.class, new JsonSerializers.BigDecimalSerializer());
        simpleModule.addDeserializer(BigDecimal.class, new JsonSerializers.BigDecimalDeserializer());

        objectMapper.registerModules(timeModule, simpleModule);

        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }

    @Bean
    public ApplicationListener<ServletWebServerInitializedEvent> webEventListener() {
        return event -> appSprings.logWebServerStartup();
    }
}
