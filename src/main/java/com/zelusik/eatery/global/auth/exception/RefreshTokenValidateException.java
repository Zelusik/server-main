package com.zelusik.eatery.global.auth.exception;

import com.zelusik.eatery.global.common.exception.UnauthorizedException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class RefreshTokenValidateException extends UnauthorizedException {

    public RefreshTokenValidateException() {
        super(CustomExceptionType.REFRESH_TOKEN_VALIDATE);
    }
}
