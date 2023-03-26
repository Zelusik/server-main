package com.zelusik.eatery.global.exception;

import com.zelusik.eatery.global.exception.common.InternalServerException;

import java.io.IOException;

public class ThumbnailImageCreateException extends InternalServerException {
    public ThumbnailImageCreateException(IOException ex) {
    }
}
