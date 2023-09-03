package com.zelusik.eatery.exception.terms_info;

import com.zelusik.eatery.exception.common.NotFoundException;

public class TermsInfoNotFoundByMemberIdException extends NotFoundException {

    public TermsInfoNotFoundByMemberIdException(long memberId) {
        super("memberId=" + memberId);
    }
}
