package com.zelusik.eatery.global.exception.member;

import com.zelusik.eatery.global.exception.common.NotFoundException;

public class MemberIdNotFoundException extends NotFoundException {

    public MemberIdNotFoundException(Long memberId) {
        super("PK = " + memberId);
    }
}
