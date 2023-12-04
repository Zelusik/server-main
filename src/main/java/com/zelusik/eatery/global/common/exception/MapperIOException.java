package com.zelusik.eatery.global.common.exception;

import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class MapperIOException extends InternalServerException {

    public MapperIOException(Throwable cause) {
        super(CustomExceptionType.MAPPER_IO_EXCEPTION, cause);
    }
}
