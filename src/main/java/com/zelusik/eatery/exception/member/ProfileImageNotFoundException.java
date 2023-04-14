package com.zelusik.eatery.exception.member;

import com.zelusik.eatery.exception.common.NotFoundException;

public class ProfileImageNotFoundException extends NotFoundException {

    public ProfileImageNotFoundException(String optionalMessage) {
        super(optionalMessage);
    }
}
