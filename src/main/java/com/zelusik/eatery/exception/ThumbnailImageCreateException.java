package com.zelusik.eatery.exception;

import com.zelusik.eatery.exception.common.InternalServerException;

import java.io.IOException;

public class ThumbnailImageCreateException extends InternalServerException {
    public ThumbnailImageCreateException(IOException ex) {
    }
}
