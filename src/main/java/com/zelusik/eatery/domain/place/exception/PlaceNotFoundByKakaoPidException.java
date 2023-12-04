package com.zelusik.eatery.domain.place.exception;

import com.zelusik.eatery.global.common.exception.NotFoundException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class PlaceNotFoundByKakaoPidException extends NotFoundException {

    public PlaceNotFoundByKakaoPidException(String kakaoPid) {
        super(CustomExceptionType.PLACE_NOT_FOUND, "kakaoPid=" + kakaoPid);
    }
}
