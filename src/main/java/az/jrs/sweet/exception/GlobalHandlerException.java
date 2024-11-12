package az.jrs.sweet.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(UserOperationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleUserOperationException(UserOperationException e) {
        log.error(e.getMessage(), e);
        return ExceptionResponse.of(
                e.getCode(),
                e.getMessage()
        );
    }

}
