package com.zelusik.eatery.global.auth.exception;

import com.zelusik.eatery.global.common.exception.UnauthorizedException;

public class AccessTokenValidateException extends UnauthorizedException {

    public AccessTokenValidateException(Throwable cause) {
        super(cause);
    }
}
