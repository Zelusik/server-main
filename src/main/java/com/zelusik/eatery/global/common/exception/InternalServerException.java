package com.zelusik.eatery.global.common.exception;

import com.zelusik.eatery.global.exception.constant.CustomExceptionType;
import org.springframework.http.HttpStatus;

public abstract class InternalServerException extends CustomException {

    public InternalServerException(CustomExceptionType exceptionType) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, exceptionType);
    }

    public InternalServerException(CustomExceptionType exceptionType, String optionalMessage) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, exceptionType, optionalMessage);
    }

    public InternalServerException(CustomExceptionType exceptionType, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, exceptionType, cause);
    }

    public InternalServerException(CustomExceptionType exceptionType, String optionalMessage, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, exceptionType, optionalMessage, cause);
    }
}
