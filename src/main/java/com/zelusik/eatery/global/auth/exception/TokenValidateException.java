package com.zelusik.eatery.global.auth.exception;

import com.zelusik.eatery.global.common.exception.UnauthorizedException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class TokenValidateException extends UnauthorizedException {

    public TokenValidateException() {
        super(CustomExceptionType.TOKEN_VALIDATE);
    }

    public TokenValidateException(String optionalMessage) {
        super(CustomExceptionType.TOKEN_VALIDATE, optionalMessage);
    }

    public TokenValidateException(Throwable cause) {
        super(CustomExceptionType.TOKEN_VALIDATE, cause);
    }
}
