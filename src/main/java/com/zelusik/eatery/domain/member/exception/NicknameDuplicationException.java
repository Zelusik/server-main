package com.zelusik.eatery.domain.member.exception;

import com.zelusik.eatery.global.common.exception.ConflictException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class NicknameDuplicationException extends ConflictException {

    public NicknameDuplicationException() {
        super(CustomExceptionType.NICKNAME_DUPLICATION);
    }
}
