package com.zelusik.eatery.global.kakao.exception;

import com.zelusik.eatery.global.common.exception.InternalServerException;

public class OpenAIServerException extends InternalServerException {

    public OpenAIServerException() {
        super();
    }

    public OpenAIServerException(Throwable cause) {
        super(cause);
    }
}
