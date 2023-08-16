package com.zelusik.eatery.exception.review;

import com.zelusik.eatery.exception.common.BadRequestException;

public class InvalidTypeOfReviewKeywordValueException extends BadRequestException {
    public InvalidTypeOfReviewKeywordValueException(String message) {
        super(message);
    }
}
