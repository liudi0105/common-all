package common.module.webmvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

@Data
public class SessionInfo {
    private CurrentUser user;

    private HttpServletRequest request;

    private HttpServletResponse response;
}
