package com.zelusik.eatery.global.kakao.exception;

import com.zelusik.eatery.global.common.exception.InternalServerException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class OpenAIServerException extends InternalServerException {

    public OpenAIServerException() {
        super(CustomExceptionType.OPEN_AI_SERVER);
    }

    public OpenAIServerException(Throwable cause) {
        super(CustomExceptionType.OPEN_AI_SERVER, cause);
    }
}
