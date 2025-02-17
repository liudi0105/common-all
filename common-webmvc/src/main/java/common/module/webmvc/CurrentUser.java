package common.module.webmvc;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CurrentUser {
    private String sessionId;
    private String nameEn;
    private String token;
    private String username;
    private String email;
    private String userId;
    private String timezone;
    private Boolean oauthLogin;
    private Set<String> roleCodes;
    private Set<String> permissionCodes;
}
