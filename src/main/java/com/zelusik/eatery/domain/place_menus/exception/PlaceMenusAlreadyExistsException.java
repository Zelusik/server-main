package com.zelusik.eatery.domain.place_menus.exception;

import com.zelusik.eatery.global.common.exception.ConflictException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class PlaceMenusAlreadyExistsException extends ConflictException {

    public PlaceMenusAlreadyExistsException(Long placeId) {
        super(CustomExceptionType.PLACE_MENUS_ALREADY_EXISTS, "placeId=" + placeId);
    }

    public PlaceMenusAlreadyExistsException(String kakaoPid) {
        super(CustomExceptionType.PLACE_MENUS_ALREADY_EXISTS, "kakaoPid=" + kakaoPid);
    }
}
