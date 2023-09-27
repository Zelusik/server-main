package com.zelusik.eatery.global.auth.exception;

import com.zelusik.eatery.global.common.exception.UnauthorizedException;

public class TokenValidateException extends UnauthorizedException {

    public TokenValidateException() {
        super();
    }

    public TokenValidateException(String optionalMessage) {
        super(optionalMessage);
    }

    public TokenValidateException(Throwable cause) {
        super(cause);
    }
}
