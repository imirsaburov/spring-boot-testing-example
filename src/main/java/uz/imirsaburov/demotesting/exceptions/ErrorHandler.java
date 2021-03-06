package uz.imirsaburov.demotesting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uz.imirsaburov.demotesting.enums.ErrorCode;
import uz.imirsaburov.demotesting.models.error.DefaultErrorDTO;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ErrorHandler {


    @ExceptionHandler(value = {Throwable.class})
    private ResponseEntity<DefaultErrorDTO> throwableHandler(Throwable throwable, HttpServletRequest httpServletRequest) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        DefaultErrorDTO dto = new DefaultErrorDTO();

        dto.setHttpMethod(httpServletRequest.getMethod());
        dto.setPath(httpServletRequest.getRequestURI());

        dto.setHttpStatusCode(httpStatus.value());
        dto.setHttpStatus(httpStatus.name());

        dto.setTimestamp(System.currentTimeMillis());

        dto.setErrorCode(ErrorCode.GLOBAL.getCode());
        dto.setErrorMessage(throwable.getMessage());

        return ResponseEntity.status(httpStatus).body(dto);
    }

    @ExceptionHandler(value = {BaseException.class})
    private ResponseEntity<DefaultErrorDTO> throwableHandler(BaseException exception, HttpServletRequest httpServletRequest) {
        HttpStatus httpStatus = exception.getHttpStatus();

        DefaultErrorDTO dto = new DefaultErrorDTO();

        dto.setHttpMethod(httpServletRequest.getMethod());
        dto.setPath(httpServletRequest.getRequestURI());

        dto.setHttpStatusCode(httpStatus.value());
        dto.setHttpStatus(httpStatus.name());

        dto.setTimestamp(System.currentTimeMillis());

        dto.setErrorCode(exception.getCode().getCode());
        dto.setErrorMessage(exception.getMessage());

        return ResponseEntity.status(httpStatus).body(dto);
    }

    @ExceptionHandler(value = {BindException.class})
    private ResponseEntity<DefaultErrorDTO> throwableHandler(BindException exception, HttpServletRequest httpServletRequest) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        DefaultErrorDTO dto = new DefaultErrorDTO();

        dto.setHttpMethod(httpServletRequest.getMethod());
        dto.setPath(httpServletRequest.getRequestURI());

        dto.setHttpStatusCode(httpStatus.value());
        dto.setHttpStatus(httpStatus.name());

        dto.setTimestamp(System.currentTimeMillis());

        dto.setErrorCode(ErrorCode.METHOD_NOT_VALID.getCode());
        dto.setErrorMessage(exception.getBindingResult().getFieldError().getDefaultMessage());

        return ResponseEntity.status(httpStatus).body(dto);
    }
}
