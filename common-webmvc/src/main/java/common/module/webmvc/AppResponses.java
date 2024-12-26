package common.module.webmvc;

import common.module.util.errors.AppError;
import common.module.util.AppJsons;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class AppResponses {

    public static void writeFile(HttpServletResponse response, Object obj) {
        response.reset();
        response.addHeader("Content-Disposition", "attachment;filename=user_export.json");
        response.setCharacterEncoding("UTF-8");
        write(response, obj, MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }

    public static void respJson(HttpServletResponse response, Object obj) {
        write(response, obj, MediaType.APPLICATION_JSON_VALUE);
    }

    public static void write(HttpServletResponse response, Object obj, String contentType) {
        response.setContentType(contentType);
        String json;
        if (obj instanceof String s) {
            json = s;
        } else {
            json = AppJsons.toJson(obj);
        }
        try (OutputStream os = response.getOutputStream()) {
            org.apache.commons.io.IOUtils.write(json.getBytes(StandardCharsets.UTF_8), os);
            os.flush();
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                throw new AppError("输出响应失败");
            }
        }
    }
}
