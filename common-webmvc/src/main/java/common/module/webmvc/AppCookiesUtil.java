package common.module.webmvc;

import jakarta.servlet.http.Cookie;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class AppCookiesUtil {

    public static Optional<String> getCookieValue(String name) {
        return getCookie(name).map(Cookie::getValue);
    }

    public static Optional<Cookie> getCookie(String name) {
        Cookie[] cookies1 = SessionInfoHolder.getSession().getRequest().getCookies();
        if (cookies1 == null || cookies1.length == 0) {
            return Optional.empty();
        }
        return Arrays.stream(cookies1)
                .filter(v -> Objects.equals(name, v.getName()))
                .findAny();
    }

    public static void expireCookie(String name) {
        getCookie(name).ifPresent(v -> {
            v.setMaxAge(0);
            v.setPath("/");
            SessionInfoHolder.getSession().getResponse().addCookie(v);
        });
    }

    public static void setLongCookie(String name, String value) {
        setCookie(name, value, Integer.MAX_VALUE);
    }

    public static void setCookie(String name, String value, Integer maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        AppSessionUtil.getResponse().addCookie(cookie);
    }
}
