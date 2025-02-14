package common.module.webmvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class AppWebRequest {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    public boolean isHttpStatusOk() {
        return response.getStatus() == 200;
    }

    public void setResponseStatus(HttpStatus httpStatus) {
        if (isHttpStatusOk()) {
            response.setStatus(httpStatus.value());
        }
    }
}
