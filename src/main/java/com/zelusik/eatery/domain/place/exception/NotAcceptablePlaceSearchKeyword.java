package com.zelusik.eatery.domain.place.exception;

import com.zelusik.eatery.global.common.exception.BadRequestException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class NotAcceptablePlaceSearchKeyword extends BadRequestException {

    public NotAcceptablePlaceSearchKeyword() {
        super(CustomExceptionType.NOT_ACCEPTABLE_PLACE_SEARCH_KEYWORD);
    }
}
