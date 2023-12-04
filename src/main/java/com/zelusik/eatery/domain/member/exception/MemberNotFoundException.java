package com.zelusik.eatery.domain.member.exception;

import com.zelusik.eatery.global.common.exception.NotFoundException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class MemberNotFoundException extends NotFoundException {

    public MemberNotFoundException() {
        super(CustomExceptionType.MEMBER_NOT_FOUND);
    }
}
