package com.zelusik.eatery.global.auth.exception;

import com.zelusik.eatery.global.common.exception.InternalServerException;

public class AppleOAuthLoginException extends InternalServerException {

    public AppleOAuthLoginException(Throwable cause) {
        super(cause);
    }
}
