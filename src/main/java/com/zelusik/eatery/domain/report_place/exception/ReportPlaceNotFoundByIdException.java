package com.zelusik.eatery.domain.report_place.exception;

import com.zelusik.eatery.global.common.exception.NotFoundException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class ReportPlaceNotFoundByIdException extends NotFoundException {

    public ReportPlaceNotFoundByIdException(Long id) {
        super(CustomExceptionType.REPORT_PLACE_NOT_FOUND, "PK = " + id);
    }
}
