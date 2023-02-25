package com.zelusik.eatery.global.exception.common;

import com.zelusik.eatery.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public abstract class NotFoundException extends CustomException {

    public NotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String optionalMessage) {
        super(HttpStatus.NOT_FOUND, optionalMessage);
    }
}
