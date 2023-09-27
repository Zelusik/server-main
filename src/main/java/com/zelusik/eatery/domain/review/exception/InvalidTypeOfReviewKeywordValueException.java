package com.zelusik.eatery.domain.review.exception;

import com.zelusik.eatery.global.common.exception.BadRequestException;

public class InvalidTypeOfReviewKeywordValueException extends BadRequestException {
    public InvalidTypeOfReviewKeywordValueException(String message) {
        super(message);
    }
}
