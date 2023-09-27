package com.zelusik.eatery.domain.review.exception;

import com.zelusik.eatery.global.common.exception.BadRequestException;

import java.util.List;

public class MismatchedMenuKeywordCountException extends BadRequestException {

    public MismatchedMenuKeywordCountException(List<String> menus, List<String> menuKeywords) {
        super(String.valueOf(menus) + menuKeywords);
    }
}
