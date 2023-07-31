package com.zelusik.eatery.exception.place;

import com.zelusik.eatery.exception.common.ConflictException;

public class PlaceAlreadyExistsException extends ConflictException {

    public PlaceAlreadyExistsException(String kakaoPid) {
        super("kakaoPid=" + kakaoPid);
    }
}
