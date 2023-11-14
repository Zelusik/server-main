package com.zelusik.eatery.global.common.exception;

import com.zelusik.eatery.global.exception.constant.CustomExceptionType;
import org.springframework.http.HttpStatus;

public abstract class UnprocessableEntityException extends CustomException {

    public UnprocessableEntityException(CustomExceptionType exceptionType) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, exceptionType);
    }

    public UnprocessableEntityException(CustomExceptionType exceptionType, String optionalMessage) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, exceptionType, optionalMessage);
    }

    public UnprocessableEntityException(CustomExceptionType exceptionType, Throwable cause) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, exceptionType, cause);
    }

    public UnprocessableEntityException(CustomExceptionType exceptionType, String optionalMessage, Throwable cause) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, exceptionType, optionalMessage, cause);
    }
}
