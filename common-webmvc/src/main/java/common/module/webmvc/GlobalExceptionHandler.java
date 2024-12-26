package common.module.webmvc;


import common.module.util.errors.AppInfo;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@Order
public class GlobalExceptionHandler {

    @Autowired
    private HttpServletResponse appResponse;

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ErrorResp constraintViolationException(ConstraintViolationException e) {
        log.warn(e.getMessage(), e);
        Map<Path, String> map = e.getConstraintViolations().stream()
                .collect(Collectors.toMap(ConstraintViolation::getPropertyPath, ConstraintViolation::getMessage));
        return new ErrorResp(map.toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ErrorResp handleValidationException(MethodArgumentNotValidException e) {
        log.warn(e.getMessage(), e);
        Map<String, String> collect = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage));
        String s = "arguments mismatch: " + collect;
        return new ErrorResp(s);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ErrorResp exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return new ErrorResp(e);
    }

    @ExceptionHandler(value = AppInfo.class)
    public ErrorResp exceptionHandler(AppInfo e) {
        return new ErrorResp(e);
    }

}
