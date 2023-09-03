package com.zelusik.eatery.exception.place;

import com.zelusik.eatery.exception.common.NotFoundException;

public class PlaceMenusNotFoundException extends NotFoundException {

    public PlaceMenusNotFoundException(Long placeId) {
        super("placeId=" + placeId);
    }

    public PlaceMenusNotFoundException(String kakaoPid) {
        super("kakaoPid=" + kakaoPid);
    }
}
