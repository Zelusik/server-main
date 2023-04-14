package com.zelusik.eatery.exception.auth;

import com.zelusik.eatery.exception.common.InternalServerException;

public class AppleOAuthLoginException extends InternalServerException {

    public AppleOAuthLoginException(Throwable cause) {
        super(cause);
    }
}
