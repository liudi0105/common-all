package common.module.webmvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@Data
public class AppSessionUtil {
    private AppSessionUtil(){}

    public static SessionInfo getSession() {
        return SessionInfoHolder.getSession();
    }

    public static CurrentUser getCurrentUser() {
        return SessionInfoHolder.getSession().getUser();
    }

    public static HttpServletRequest getRequest() {
        return SessionInfoHolder.getSession().getRequest();
    }


    public static HttpServletResponse getResponse() {
        return SessionInfoHolder.getSession().getResponse();
    }

    public static Optional<String> getCookieValue(String name) {
        return AppCookiesUtil.getCookieValue(name);
    }

    public static boolean isHttpStatusOk() {
        return SessionInfoHolder.getSession().getResponse().getStatus() == 200;
    }

    public static void setResponseStatus(HttpStatus httpStatus) {
        if (isHttpStatusOk()) {
            SessionInfoHolder.getSession().getResponse().setStatus(httpStatus.value());
        }
    }
}
