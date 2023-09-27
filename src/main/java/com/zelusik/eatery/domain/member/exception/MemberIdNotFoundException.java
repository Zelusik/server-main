package com.zelusik.eatery.domain.member.exception;

import com.zelusik.eatery.global.common.exception.NotFoundException;

public class MemberIdNotFoundException extends NotFoundException {

    public MemberIdNotFoundException(Long memberId) {
        super("PK = " + memberId);
    }
}
