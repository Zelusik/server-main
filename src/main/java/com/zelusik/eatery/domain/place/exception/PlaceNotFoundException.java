package com.zelusik.eatery.domain.place.exception;

import com.zelusik.eatery.global.common.exception.NotFoundException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class PlaceNotFoundException extends NotFoundException {

    public PlaceNotFoundException() {
        super(CustomExceptionType.PLACE_NOT_FOUND);
    }
}
