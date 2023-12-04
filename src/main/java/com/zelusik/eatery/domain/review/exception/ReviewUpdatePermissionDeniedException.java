package com.zelusik.eatery.domain.review.exception;

import com.zelusik.eatery.global.common.exception.ForbiddenException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class ReviewUpdatePermissionDeniedException extends ForbiddenException {

    public ReviewUpdatePermissionDeniedException() {
        super(CustomExceptionType.REVIEW_UPDATE_PERMISSION_DENIED);
    }
}
