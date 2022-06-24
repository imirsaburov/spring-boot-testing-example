package uz.imirsaburov.demotesting.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import uz.imirsaburov.demotesting.enums.ErrorCode;

@Getter
public class BaseException extends RuntimeException {

    private final ErrorCode code;
    private final HttpStatus httpStatus;

    public BaseException(String message, HttpStatus httpStatus, ErrorCode errorCode) {
        super(message);
        this.code = errorCode;
        this.httpStatus = httpStatus;
    }
}
