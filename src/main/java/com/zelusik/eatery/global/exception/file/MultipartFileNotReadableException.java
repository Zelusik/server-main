package com.zelusik.eatery.global.exception.file;

import com.zelusik.eatery.global.exception.common.BadRequestException;

public class MultipartFileNotReadableException extends BadRequestException {

    public MultipartFileNotReadableException(Throwable cause) {
        super(cause);
    }
}
