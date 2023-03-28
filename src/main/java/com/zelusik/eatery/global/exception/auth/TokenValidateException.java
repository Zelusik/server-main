package com.zelusik.eatery.global.exception.auth;

import com.zelusik.eatery.global.exception.common.UnauthorizedException;

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
