package com.zelusik.eatery.domain.review.exception;

import com.zelusik.eatery.global.common.exception.BadRequestException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class NotAcceptableReviewKeyword extends BadRequestException {

    public NotAcceptableReviewKeyword(String description) {
        super(CustomExceptionType.NOT_ACCEPTABLE_REVIEW_KEYWORD, "입력된 keyword=" + description);
    }
}
