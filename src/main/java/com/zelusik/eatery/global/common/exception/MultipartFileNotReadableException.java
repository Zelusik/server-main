package com.zelusik.eatery.global.common.exception;

import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class MultipartFileNotReadableException extends BadRequestException {

    public MultipartFileNotReadableException(Throwable cause) {
        super(CustomExceptionType.MULTIPART_FILE_NOT_READABLE, cause);
    }
}
