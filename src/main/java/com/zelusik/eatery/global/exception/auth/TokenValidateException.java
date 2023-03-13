package com.zelusik.eatery.global.exception.auth;

import com.zelusik.eatery.global.exception.common.InternalServerException;

public class TokenValidateException extends InternalServerException {

    public TokenValidateException() {
        super();
    }

    public TokenValidateException(Throwable cause) {
        super(cause);
    }
}
