package com.zelusik.eatery.domain.place.exception;

import com.zelusik.eatery.global.common.exception.ConflictException;

public class PlaceMenusAlreadyExistsException extends ConflictException {

    public PlaceMenusAlreadyExistsException(Long placeId) {
        super("placeId=" + placeId);
    }

    public PlaceMenusAlreadyExistsException(String kakaoPid) {
        super("kakaoPid=" + kakaoPid);
    }
}
