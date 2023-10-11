package com.zelusik.eatery.domain.review.exception;

import com.zelusik.eatery.global.common.exception.BadRequestException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class InvalidTypeOfReviewKeywordValueException extends BadRequestException {
    public InvalidTypeOfReviewKeywordValueException(String message) {
        super(CustomExceptionType.INVALID_TYPE_OF_REVIEW_KEYWORD_VALUE, message);
    }
}
