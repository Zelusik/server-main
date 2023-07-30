package com.zelusik.eatery.exception.place;

import com.zelusik.eatery.exception.common.ConflictException;

public class PlaceMenusAlreadyExistsException extends ConflictException {

    public PlaceMenusAlreadyExistsException(Long placeId) {
        super("placeId=" + placeId);
    }
}
