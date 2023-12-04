package com.zelusik.eatery.global.common.exception;

import com.zelusik.eatery.global.exception.constant.CustomExceptionType;
import org.springframework.http.HttpStatus;

public abstract class UnauthorizedException extends CustomException {

    public UnauthorizedException(CustomExceptionType exceptionType) {
        super(HttpStatus.UNAUTHORIZED, exceptionType);
    }

    public UnauthorizedException(CustomExceptionType exceptionType, String optionalMessage) {
        super(HttpStatus.UNAUTHORIZED, exceptionType, optionalMessage);
    }

    public UnauthorizedException(CustomExceptionType exceptionType, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, exceptionType, cause);
    }

    public UnauthorizedException(CustomExceptionType exceptionType, String optionalMessage, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, exceptionType, optionalMessage, cause);
    }
}
