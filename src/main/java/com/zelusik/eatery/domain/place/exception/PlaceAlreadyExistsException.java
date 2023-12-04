package com.zelusik.eatery.domain.place.exception;

import com.zelusik.eatery.global.common.exception.ConflictException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class PlaceAlreadyExistsException extends ConflictException {

    public PlaceAlreadyExistsException(String kakaoPid) {
        super(CustomExceptionType.PLACE_ALREADY_EXISTS, "kakaoPid=" + kakaoPid);
    }
}
