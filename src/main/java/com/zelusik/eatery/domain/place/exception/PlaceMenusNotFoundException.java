package com.zelusik.eatery.domain.place.exception;

import com.zelusik.eatery.global.common.exception.NotFoundException;

public class PlaceMenusNotFoundException extends NotFoundException {

    public PlaceMenusNotFoundException(Long placeId) {
        super("placeId=" + placeId);
    }

    public PlaceMenusNotFoundException(String kakaoPid) {
        super("kakaoPid=" + kakaoPid);
    }
}
