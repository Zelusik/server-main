package com.zelusik.eatery.global.common.exception;

import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

import java.io.IOException;

public class ThumbnailImageCreationException extends InternalServerException {

    public ThumbnailImageCreationException(IOException ex) {
        super(CustomExceptionType.THUMBNAIL_IMAGE_CREATE, ex);
    }
}
