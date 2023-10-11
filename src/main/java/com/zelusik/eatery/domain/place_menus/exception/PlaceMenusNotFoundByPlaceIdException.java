package com.zelusik.eatery.domain.place_menus.exception;

import com.zelusik.eatery.global.common.exception.NotFoundException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class PlaceMenusNotFoundByPlaceIdException extends NotFoundException {

    public PlaceMenusNotFoundByPlaceIdException(Long placeId) {
        super(CustomExceptionType.PLACE_MENUS_NOT_FOUND, "placeId=" + placeId);
    }
}
