package com.zelusik.eatery.exception;

import com.zelusik.eatery.exception.common.InternalServerException;

public class MapperIOException extends InternalServerException {

    public MapperIOException(Throwable cause) {
        super(cause);
    }
}
