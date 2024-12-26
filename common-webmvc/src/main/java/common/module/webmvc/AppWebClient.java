package common.module.webmvc;

import com.google.common.collect.Lists;
import common.module.util.errors.AppError;
import common.module.util.AppJsons;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppWebClient {

    private final RestTemplate restTemplate;

    public AppWebClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AppWebClient() {
        this.restTemplate = AppWebClient.newRestTemplate();
    }

    public static RestTemplate newRestTemplate() {
        return new RestTemplateBuilder()
                .additionalMessageConverters(List.of(
                        new ByteArrayHttpMessageConverter(),
                        new StringHttpMessageConverter(),
                        new ResourceHttpMessageConverter(),
                        new AllEncompassingFormHttpMessageConverter(),
//                        new Jaxb2RootElementHttpMessageConverter(),
                        WebMvcConfiguration.jsonConverter()
                ))
                .requestFactory(WebMvcConfiguration::unsafeRequestFactory)
                .build();
    }

    public <T> ResponseEntity<T> request(URI url, HttpMethod method, Object params, HttpHeaders headers, Class<T> respType) {
        return request(url, method, params, headers, toTypeRef(respType));
    }

    protected <T> ResponseEntity<T> request(URI uri, HttpMethod method, Object body, HttpHeaders headers, ParameterizedTypeReference<T> respType) {
        Object param = body;
        if (MediaType.APPLICATION_FORM_URLENCODED.equals(headers.getContentType()) && body != null) {
            LinkedMultiValueMap<Object, Object> mmap = new LinkedMultiValueMap<>();
            new LinkedMultiValueMap<>();
            AppJsons.toMap(body).forEach(mmap::add);
            param = mmap;
        }
        RequestEntity<Object> requestEntity = new RequestEntity<>(param, headers, method, uri);

        ResponseEntity<T> exchange = restTemplate.exchange(requestEntity, respType);

        if (exchange.getStatusCode().isError()) {
            HttpStatus httpStatus = HttpStatus.valueOf(exchange.getStatusCode().value());
            throw new AppError(httpStatus.value() + ": " + httpStatus.getReasonPhrase());
        }

        return exchange;
    }

    public static <T> ParameterizedTypeReference<T> toTypeRef(Class<T> clazz) {
        return new ParameterizedTypeReference<>() {
            @Override
            public Type getType() {
                return clazz;
            }
        };
    }

    public <T> T postJsonForJson(String url, Object params, HttpHeaders headers, Class<T> clazz) {
        return requestJsonForJson(url, HttpMethod.POST, params, headers, toTypeRef(clazz));
    }

    public <T> T postJsonForJson(String url, Object params, HttpHeaders headers, ParameterizedTypeReference<T> respType) {
        return requestJsonForJson(url, HttpMethod.POST, params, headers, respType);
    }

    public <T> T postJsonForJson(String url, Object params, Class<T> clazz) {
        return postJsonForJson(url, params, toTypeRef(clazz));
    }

    public <T> T postJsonForJson(String url, Object params, ParameterizedTypeReference<T> respType) {
        return requestJsonForJson(url, HttpMethod.POST, params, null, respType);
    }

    private final ParameterizedTypeReference<HashMap<String, Object>> defaultRespType = new ParameterizedTypeReference<>() {
    };

    public Map<String, Object> postJson(String url, Object params) {
        return postJsonForJson(url, params, defaultRespType);
    }

    public <T> T requestJsonForJson(String url, HttpMethod method, Object body, HttpHeaders headers, Class<T> respType) {
        return requestJsonForJson(url, method, body, headers, toTypeRef(respType));
    }

    public <T> T requestJsonForJson(String url, HttpMethod method, Object body, HttpHeaders headers, ParameterizedTypeReference<T> respType) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.setAccept(Lists.newArrayList(MediaType.APPLICATION_JSON));
        ResponseEntity<T> request = request(URI.create(url), method, body, headers, respType);
        return request.getBody();
    }

    public <T> T postParamForJson(String url, Map<String, String> params, HttpHeaders headers, Class<T> respType) {
        return postParamForJson(url, params, headers, toTypeRef(respType));
    }

    public <T> T postParamForJson(String url, Map<String, String> params, HttpHeaders headers, ParameterizedTypeReference<T> respType) {
        return requestParamForJson(url, HttpMethod.POST, params, headers, respType, false);
    }

    public <T> T postParamForJson(String url, Map<String, String> params, Class<T> respType) {
        return postParamForJson(url, params, toTypeRef(respType));
    }

    public <T> T postParamForJson(String url, Map<String, String> params, ParameterizedTypeReference<T> respType) {
        return requestParamForJson(url, HttpMethod.POST, params, null, respType, false);
    }

    public <T> T postUrlParamForJson(String url, Object params, HttpHeaders headers, Class<T> respType) {
        return postUrlParamForJson(url, params, headers, toTypeRef(respType));
    }

    public <T> T postUrlParamForJson(String url, Object params, HttpHeaders headers, ParameterizedTypeReference<T> respType) {
        return requestParamForJson(url, HttpMethod.POST, params, headers, respType);
    }

    public <T> T postUrlParamForJson(String url, Object params, Class<T> respType) {
        return postUrlParamForJson(url, params, toTypeRef(respType));
    }

    public <T> T postUrlParamForJson(String url, Object params, ParameterizedTypeReference<T> respType) {
        return postUrlParamForJson(url, params, null, respType);
    }

    public <T> T requestParamForJson(String url, HttpMethod method, Object body, HttpHeaders headers, ParameterizedTypeReference<T> respType) {
        return requestParamForJson(url, method, body, headers, respType, false);
    }

    public <T> T requestParamForJson(String url, HttpMethod method, Object body, HttpHeaders headers, ParameterizedTypeReference<T> respType, boolean uriParam) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
        LinkedMultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        Map<String, String> map = AppJsons.toMap(body);
        map.forEach(param::add);
        if (uriParam) {
            uriComponentsBuilder.queryParams(param);
        }
        URI uri = uriComponentsBuilder.build().toUri();

        if (headers == null) {
            headers = new HttpHeaders();
        }
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        headers.setAccept(Lists.newArrayList(MediaType.APPLICATION_JSON));
        Map<String, String> bodyParam = uriParam ? null : map;
        ResponseEntity<T> request = request(uri, method, bodyParam, headers, respType);
        return request.getBody();
    }

    public <T> T getForJson(String url, HttpHeaders headers, Class<T> respType) {
        return requestParamForJson(url, HttpMethod.GET, Map.of(), headers, toTypeRef(respType));
    }

    public <T> T getForJson(String url, HttpHeaders headers, ParameterizedTypeReference<T> respType) {
        return requestParamForJson(url, HttpMethod.GET, Map.of(), headers, respType);
    }

    public <T> T getParamForJson(String url, Map<String, String> params, HttpHeaders headers, Class<T> respType) {
        return getParamForJson(url, params, headers, toTypeRef(respType));
    }

    public <T> T getParamForJson(String url, Map<String, String> params, HttpHeaders headers, ParameterizedTypeReference<T> respType) {
        return requestParamForJson(url, HttpMethod.GET, params, headers, respType, true);
    }

}
