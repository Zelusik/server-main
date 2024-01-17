package com.zelusik.eatery.domain.report_review.exception;

import com.zelusik.eatery.global.common.exception.NotFoundException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class ReportReviewNotFoundByIdException extends NotFoundException {

    public ReportReviewNotFoundByIdException(Long id) {
        super(CustomExceptionType.REPORT_REVIEW_NOT_FOUND, "PK = " + id);
    }
}
