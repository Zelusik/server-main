package com.zelusik.eatery.global.common.exception;

import com.zelusik.eatery.global.exception.constant.CustomExceptionType;
import org.springframework.http.HttpStatus;

public abstract class NotFoundException extends CustomException {

    public NotFoundException(CustomExceptionType exceptionType) {
        super(HttpStatus.NOT_FOUND, exceptionType);
    }

    public NotFoundException(CustomExceptionType exceptionType, String optionalMessage) {
        super(HttpStatus.NOT_FOUND, exceptionType, optionalMessage);
    }

    public NotFoundException(CustomExceptionType exceptionType, Throwable cause) {
        super(HttpStatus.NOT_FOUND, exceptionType, cause);
    }

    public NotFoundException(CustomExceptionType exceptionType, String optionalMessage, Throwable cause) {
        super(HttpStatus.NOT_FOUND, exceptionType, optionalMessage, cause);
    }
}
