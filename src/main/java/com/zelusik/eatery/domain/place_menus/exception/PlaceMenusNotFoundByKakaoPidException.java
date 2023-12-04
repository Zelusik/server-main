package com.zelusik.eatery.domain.place_menus.exception;

import com.zelusik.eatery.global.common.exception.NotFoundException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class PlaceMenusNotFoundByKakaoPidException extends NotFoundException {

    public PlaceMenusNotFoundByKakaoPidException(String kakaoPid) {
        super(CustomExceptionType.PLACE_MENUS_NOT_FOUND, "kakaoPid=" + kakaoPid);
    }
}
