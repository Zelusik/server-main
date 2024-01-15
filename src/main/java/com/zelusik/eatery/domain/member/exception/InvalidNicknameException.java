package com.zelusik.eatery.domain.member.exception;

import com.zelusik.eatery.global.common.exception.UnprocessableEntityException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class InvalidNicknameException extends UnprocessableEntityException {

    public InvalidNicknameException(String optionalMessage) {
        super(CustomExceptionType.INVALID_NICKNAME, optionalMessage);
    }
}
