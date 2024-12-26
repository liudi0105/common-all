package common.module.jpa;

import common.module.util.AppErrors;
import common.module.webmvc.ErrorResp;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@Order(0)
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class JpaExceptionHandler {

    @ExceptionHandler(TransactionSystemException.class)
    public ErrorResp handleTransactionSystemException(TransactionSystemException e) {
        log.error(e.getMessage(), e);
        Map<Path, String> collect = AppErrors.findCause(e, ConstraintViolationException.class)
                .stream()
                .flatMap(v -> v.getConstraintViolations().stream())
                .collect(Collectors.toMap(ConstraintViolation::getPropertyPath, ConstraintViolation::getMessage));
        return new ErrorResp(collect.toString());
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ErrorResp handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error(e.getMessage(), e);
        return AppErrors.findCause(e, PSQLException.class)
                .map(ErrorResp::new)
                .orElse(new ErrorResp(e));
    }
}
