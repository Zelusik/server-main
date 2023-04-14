package com.zelusik.eatery.exception.review;

import com.zelusik.eatery.exception.common.BadRequestException;

public class NotAcceptableReviewKeyword extends BadRequestException {

    public NotAcceptableReviewKeyword(String description) {
        super("입력된 keyword=" + description);
    }
}
