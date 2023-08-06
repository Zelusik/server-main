package com.zelusik.eatery.exception.open_ai;

import com.zelusik.eatery.exception.common.InternalServerException;

public class OpenAIServerException extends InternalServerException {

    public OpenAIServerException() {
        super();
    }

    public OpenAIServerException(Throwable cause) {
        super(cause);
    }
}
