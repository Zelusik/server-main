package com.zelusik.eatery.domain.review.exception;

import com.zelusik.eatery.global.common.exception.BadRequestException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

import java.util.List;

public class MismatchedMenuKeywordCountException extends BadRequestException {

    public MismatchedMenuKeywordCountException(List<String> menus, List<String> menuKeywords) {
        super(CustomExceptionType.MISMATCHED_MENU_KEYWORD_COUNT, String.valueOf(menus) + menuKeywords);
    }
}
