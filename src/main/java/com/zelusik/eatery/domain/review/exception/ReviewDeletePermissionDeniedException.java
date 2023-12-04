package com.zelusik.eatery.domain.review.exception;

import com.zelusik.eatery.global.common.exception.ForbiddenException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class ReviewDeletePermissionDeniedException extends ForbiddenException {

    public ReviewDeletePermissionDeniedException() {
        super(CustomExceptionType.REVIEW_DELETE_PERMISSION_DENIED);
    }
}
