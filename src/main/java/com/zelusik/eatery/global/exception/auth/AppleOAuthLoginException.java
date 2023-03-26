package com.zelusik.eatery.global.exception.auth;

import com.zelusik.eatery.global.exception.common.InternalServerException;

public class AppleOAuthLoginException extends InternalServerException {

    public AppleOAuthLoginException(Throwable cause) {
        super(cause);
    }
}
