package common.module.util.errors;

import common.module.util.AppStrings;
import lombok.Getter;

/**
 * throw with error log record
 */
@Getter
public class AppError extends RuntimeException {

    private final String code;
    private final String msg;

    public AppError(Throwable e) {
        super(e);
        if (e instanceof AppWarning) {
            AppWarning er = (AppWarning) e;
            this.code = er.getCode();
            this.msg = er.getMsg();
        } else if (e instanceof AppError) {
            AppError er = (AppError) e;
            this.code = er.getCode();
            this.msg = er.getMsg();
        } else {
            this.code = "UNKNOWN_ERROR";
            this.msg = e.getMessage();
        }
    }

    public AppError(String template, Object... params) {
        super(AppStrings.sprintf(template, params));
        this.code = "UNKNOWN_ERROR";
        this.msg = super.getMessage();
    }

    public AppError(AppError iAppError) {
        super(iAppError.getMsg());
        this.code = iAppError.getCode();
        this.msg = iAppError.getMsg();
    }

}
