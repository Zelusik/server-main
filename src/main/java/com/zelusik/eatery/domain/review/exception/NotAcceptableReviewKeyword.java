package com.zelusik.eatery.domain.review.exception;

import com.zelusik.eatery.global.common.exception.BadRequestException;

public class NotAcceptableReviewKeyword extends BadRequestException {

    public NotAcceptableReviewKeyword(String description) {
        super("입력된 keyword=" + description);
    }
}
