package az.jrs.sweet.exception;

import lombok.Getter;

@Getter
public class UserOperationException extends RuntimeException {

    private final String code;

    public UserOperationException(String message,String code) {
        super(message);
        this.code = code;
    }
}
