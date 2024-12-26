package common.module.util.errors;

import lombok.Getter;

/**
 * throw without any operation
 */
@Getter
public class OqError extends RuntimeException {
    private final String code;
    private final String msg;

    public OqError(Throwable e) {
        super(e);
        this.code = "UNKNOWN_ERROR";
        this.msg = e.getMessage();
    }

    public OqError(String code, String message) {
        super(message);
        this.code = code;
        this.msg = message;
    }

    public OqError(String message) {
        super(message);
        this.code = "UNKNOWN_ERROR";
        this.msg = message;
    }

    public OqError(OqError iAppError) {
        super(iAppError.getMsg());
        this.code = iAppError.getCode();
        this.msg = iAppError.getMsg();
    }

}
