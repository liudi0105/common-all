package common.module.webmvc;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Getter
public class AppCookies {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    public Optional<String> getCookieValue(String name) {
        return getCookie(name).map(Cookie::getValue);
    }

    public Optional<Cookie> getCookie(String name) {
        Cookie[] cookies1 = request.getCookies();
        if (cookies1 == null || cookies1.length == 0) {
            return Optional.empty();
        }
        return Arrays.stream(cookies1)
                .filter(v -> Objects.equals(name, v.getName()))
                .findAny();
    }

    public void expireCookie(String name) {
        getCookie(name).ifPresent(v -> {
            v.setMaxAge(0);
            v.setPath("/");
            response.addCookie(v);
        });
    }

    public void setLongCookie(String name, String value) {
        setCookie(name, value, Integer.MAX_VALUE);
    }

    public void setCookie(String name, String value, Integer maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}
