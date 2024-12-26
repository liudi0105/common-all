package common.module.test;

import common.module.util.AppStrings;
import common.module.util.AppYamls;
import common.module.webmvc.AppWebClient;
import common.module.dto.ValueWrapper;
import org.springframework.core.ParameterizedTypeReference;

public class ApiTestClient {

    protected final AppWebClient appWebClient = new AppWebClient();

    protected final TestConfig testConfig;

    protected final String group;

    public ApiTestClient(String group) {
        this.group = group;
        this.testConfig = AppYamls.loadClasspath("/config/test.yml", TestConfig.class);
    }

    public <T> T apiPostValueForJson(String api, Object params, Class<T> typeReference) {
        return appWebClient.postJsonForJson(preRequest(api), new ValueWrapper<>(params), typeReference);
    }

    public <T> T apiPostForJson(String api, Class<T> typeReference) {

        return appWebClient.postJsonForJson(preRequest(api), null, typeReference);
    }

    public <T> T apiPostJsonForJson(String api, Object params, Class<T> typeReference) {
        return appWebClient.postJsonForJson(preRequest(api), params, typeReference);
    }

    public <T> T apiPostJsonForJson(String api, Object params, ParameterizedTypeReference<T> typeReference) {
        return appWebClient.postJsonForJson(preRequest(api), params, typeReference);
    }

    protected String preRequest(String api) {
        return AppStrings.joinPath(testConfig.getApiUrl(), group, api);
    }

}
