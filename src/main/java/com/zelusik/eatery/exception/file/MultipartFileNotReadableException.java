package com.zelusik.eatery.exception.file;

import com.zelusik.eatery.exception.common.BadRequestException;

public class MultipartFileNotReadableException extends BadRequestException {

    public MultipartFileNotReadableException(Throwable cause) {
        super(cause);
    }
}
