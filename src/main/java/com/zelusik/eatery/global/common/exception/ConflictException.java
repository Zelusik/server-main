package com.zelusik.eatery.global.common.exception;

import com.zelusik.eatery.global.exception.constant.CustomExceptionType;
import org.springframework.http.HttpStatus;

public abstract class ConflictException extends CustomException {

    public ConflictException(CustomExceptionType exceptionType) {
        super(HttpStatus.CONFLICT, exceptionType);
    }

    public ConflictException(CustomExceptionType exceptionType, String optionalMessage) {
        super(HttpStatus.CONFLICT, exceptionType, optionalMessage);
    }

    public ConflictException(CustomExceptionType exceptionType, Throwable cause) {
        super(HttpStatus.CONFLICT, exceptionType, cause);
    }

    public ConflictException(CustomExceptionType exceptionType, String optionalMessage, Throwable cause) {
        super(HttpStatus.CONFLICT, exceptionType, optionalMessage, cause);
    }
}
