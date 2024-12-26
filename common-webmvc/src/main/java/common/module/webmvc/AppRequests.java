package common.module.webmvc;

import common.module.util.errors.AppError;
import common.module.util.AppJsons;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class AppRequests {
    public static <T> T parseBody(HttpServletRequest request, Class<T> clazz) {
        try {
            return AppJsons.objectMapper().readValue(request.getInputStream(), clazz);
        } catch (IOException e) {
            throw new AppError("解析请求体失败");
        }
    }

}
