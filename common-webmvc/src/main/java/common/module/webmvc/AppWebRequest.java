package common.module.webmvc;

import org.springframework.http.HttpStatus;

/**
 * 后续废除该类，请使用{@link AppSessionUtil}处理当前请求及用户相关信息
 */
@Deprecated
public class AppWebRequest {

    public static boolean isHttpStatusOk() {
        return SessionInfoHolder.getSession().getResponse().getStatus() == 200;
    }

    public static void setResponseStatus(HttpStatus httpStatus) {
        if (isHttpStatusOk()) {
            SessionInfoHolder.getSession().getResponse().setStatus(httpStatus.value());
        }
    }
}
