package com.zelusik.eatery.global.exception.review;

import com.zelusik.eatery.global.exception.common.BadRequestException;

public class NotAcceptableReviewKeyword extends BadRequestException {

    public NotAcceptableReviewKeyword(String description) {
        super("입력된 keyword=" + description);
    }
}
