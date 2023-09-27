package com.zelusik.eatery.domain.member.exception;

import com.zelusik.eatery.global.common.exception.NotFoundException;

public class ProfileImageNotFoundException extends NotFoundException {

    public ProfileImageNotFoundException(String optionalMessage) {
        super(optionalMessage);
    }
}
