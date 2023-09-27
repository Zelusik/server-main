package com.zelusik.eatery.domain.place.exception;

import com.zelusik.eatery.global.common.exception.NotFoundException;

public class PlaceNotFoundException extends NotFoundException {

    public PlaceNotFoundException() {
        super();
    }

    private PlaceNotFoundException(String optionalMessage) {
        super(optionalMessage);
    }

    private PlaceNotFoundException(Throwable cause) {
        super(cause);
    }

    private PlaceNotFoundException(String optionalMessage, Throwable cause) {
        super(optionalMessage, cause);
    }

    public static PlaceNotFoundException kakaoPid(String kakaoPid) {
        return new PlaceNotFoundException("kakaoPid=" + kakaoPid);
    }
}
