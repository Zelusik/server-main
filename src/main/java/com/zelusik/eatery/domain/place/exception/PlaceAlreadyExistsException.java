package com.zelusik.eatery.domain.place.exception;

import com.zelusik.eatery.global.common.exception.ConflictException;

public class PlaceAlreadyExistsException extends ConflictException {

    public PlaceAlreadyExistsException(String kakaoPid) {
        super("kakaoPid=" + kakaoPid);
    }
}
