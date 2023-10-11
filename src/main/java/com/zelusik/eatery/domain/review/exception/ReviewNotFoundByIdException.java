package com.zelusik.eatery.domain.review.exception;

import com.zelusik.eatery.global.common.exception.NotFoundException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class ReviewNotFoundByIdException extends NotFoundException {

    public ReviewNotFoundByIdException(Long id) {
        super(CustomExceptionType.REVIEW_NOT_FOUND_BY_ID, "id=" + id);
    }
}
