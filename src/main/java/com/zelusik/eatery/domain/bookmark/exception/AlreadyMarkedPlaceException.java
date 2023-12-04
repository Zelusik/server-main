package com.zelusik.eatery.domain.bookmark.exception;

import com.zelusik.eatery.global.common.exception.ConflictException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class AlreadyMarkedPlaceException extends ConflictException {

    public AlreadyMarkedPlaceException() {
        super(CustomExceptionType.ALREADY_MARKED_PLACE);
    }
}
