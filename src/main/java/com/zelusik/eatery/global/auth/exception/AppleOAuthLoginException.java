package com.zelusik.eatery.global.auth.exception;

import com.zelusik.eatery.global.common.exception.InternalServerException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class AppleOAuthLoginException extends InternalServerException {

    public AppleOAuthLoginException(Throwable cause) {
        super(CustomExceptionType.APPLE_OAUTH_LOGIN, cause);
    }
}
