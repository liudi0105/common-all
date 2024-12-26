package common.module.webmvc;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

@Slf4j
public class FileDownloadUtils {

    /**
     * 通过 HttpClient 5 从文件 URL 下载文件并将其返回给客户端
     */
    public static void downloadFileFromUrl(String fileUrl, HttpServletResponse response) {
        try (var httpClient = HttpClients.createDefault()) {  // 创建 HttpClient
            // 创建 GET 请求
            var httpGet = new HttpGet(fileUrl);

            try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) { // 执行请求
                if (httpResponse.getCode() != 200) {
                    throw new HttpResponseException(httpResponse.getCode(), httpResponse.getReasonPhrase());
                }

                var entity = httpResponse.getEntity();

                // 获取文件名和内容类型
                String fileName = getFileNameFromUrl(fileUrl);
                String contentType = entity.getContentType() != null ? entity.getContentType() : "application/octet-stream";

                // 设置响应头
                response.setContentType(contentType);
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

                // 从输入流中读取数据并写入到输出流
                try (InputStream inputStream = entity.getContent();
                     OutputStream outputStream = response.getOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从 URL 中提取文件名
     */
    private static String getFileNameFromUrl(String fileUrl) {
        try {
            return Paths.get(new java.net.URI(fileUrl).getPath()).getFileName().toString();
        } catch (Exception e) {
            return "downloadedFile";  // 默认文件名
        }
    }
}