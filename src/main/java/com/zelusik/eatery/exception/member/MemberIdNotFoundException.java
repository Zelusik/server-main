package com.zelusik.eatery.exception.member;

import com.zelusik.eatery.exception.common.NotFoundException;

public class MemberIdNotFoundException extends NotFoundException {

    public MemberIdNotFoundException(Long memberId) {
        super("PK = " + memberId);
    }
}
