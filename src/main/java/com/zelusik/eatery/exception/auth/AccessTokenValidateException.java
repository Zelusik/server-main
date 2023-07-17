package com.zelusik.eatery.exception.auth;

import com.zelusik.eatery.exception.common.UnauthorizedException;

public class AccessTokenValidateException extends UnauthorizedException {

    public AccessTokenValidateException(Throwable cause) {
        super(cause);
    }
}
