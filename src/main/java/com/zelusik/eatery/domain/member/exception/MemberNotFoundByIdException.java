package com.zelusik.eatery.domain.member.exception;

import com.zelusik.eatery.global.common.exception.NotFoundException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class MemberNotFoundByIdException extends NotFoundException {

    public MemberNotFoundByIdException(Long id) {
        super(CustomExceptionType.MEMBER_NOT_FOUND, "PK = " + id);
    }
}
