package common.module.util.errors;

public class NotLoginError extends RuntimeException {
    public NotLoginError(String message) {
        super(message);
    }
}
