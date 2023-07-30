package com.zelusik.eatery.exception.place;

import com.zelusik.eatery.exception.common.NotFoundException;

public class PlaceMenusNotFoundByPlaceIdException extends NotFoundException {

    public PlaceMenusNotFoundByPlaceIdException(Long placeId) {
        super("placeId=" + placeId);
    }
}
