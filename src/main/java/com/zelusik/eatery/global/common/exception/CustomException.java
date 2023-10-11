package com.zelusik.eatery.global.common.exception;

import com.zelusik.eatery.global.exception.constant.CustomExceptionType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class CustomException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;
    private final Throwable cause;

    public CustomException(HttpStatus httpStatus, CustomExceptionType exceptionType) {
        this.httpStatus = httpStatus;
        this.code = exceptionType.getCode();
        this.message = exceptionType.getMessage();
        this.cause = null;
    }

    public CustomException(HttpStatus httpStatus, CustomExceptionType exceptionType, String optionalMessage) {
        this.httpStatus = httpStatus;
        this.code = exceptionType.getCode();
        this.message = exceptionType.getMessage() + " " + optionalMessage;
        this.cause = null;
    }

    public CustomException(HttpStatus httpStatus, CustomExceptionType exceptionType, Throwable cause) {
        this.httpStatus = httpStatus;
        this.code = exceptionType.getCode();
        this.message = exceptionType.getMessage();
        this.cause = cause;
    }

    public CustomException(HttpStatus httpStatus, CustomExceptionType exceptionType, String optionalMessage, Throwable cause) {
        this.httpStatus = httpStatus;
        this.code = exceptionType.getCode();
        this.message = exceptionType.getMessage() + " " + optionalMessage;
        this.cause = cause;
    }
}
