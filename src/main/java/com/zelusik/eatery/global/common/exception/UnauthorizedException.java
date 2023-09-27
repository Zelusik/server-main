package com.zelusik.eatery.global.common.exception;

import org.springframework.http.HttpStatus;

public abstract class UnauthorizedException extends CustomException {

    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(String optionalMessage) {
        super(HttpStatus.UNAUTHORIZED, optionalMessage);
    }

    public UnauthorizedException(Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, cause);
    }

    public UnauthorizedException(String optionalMessage, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, optionalMessage, cause);
    }
}
