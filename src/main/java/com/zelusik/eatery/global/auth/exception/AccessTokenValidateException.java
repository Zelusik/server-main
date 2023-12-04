package com.zelusik.eatery.global.auth.exception;

import com.zelusik.eatery.global.common.exception.UnauthorizedException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class AccessTokenValidateException extends UnauthorizedException {

    public AccessTokenValidateException(Throwable cause) {
        super(CustomExceptionType.ACCESS_TOKEN_VALIDATE, cause);
    }
}
