package com.zelusik.eatery.global.exception.member;

import com.zelusik.eatery.global.exception.common.NotFoundException;

public class ProfileImageNotFoundException extends NotFoundException {

    public ProfileImageNotFoundException(String optionalMessage) {
        super(optionalMessage);
    }
}
