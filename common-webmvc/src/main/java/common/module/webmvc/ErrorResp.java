package common.module.webmvc;

import common.module.util.errors.AppError;
import common.module.util.errors.AppWarning;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResp {
    private String code;
    private String message;

    public ErrorResp(Throwable e) {
        if (e instanceof AppWarning) {
            AppWarning er = (AppWarning) e;
            this.code = er.getCode();
            this.message = er.getMsg();
        } else if (e instanceof AppError) {
            AppError er = (AppError) e;
            this.code = er.getCode();
            this.message = er.getMsg();
        } else {
            this.code = "UNKNOWN_ERROR";
            this.message = e.getMessage();
        }
    }

    public ErrorResp(String message) {
        this.code = "UNKNOWN_ERROR";
        this.message = message;
    }

    public ErrorResp(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
