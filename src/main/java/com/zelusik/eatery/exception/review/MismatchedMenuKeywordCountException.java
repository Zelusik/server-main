package com.zelusik.eatery.exception.review;

import com.zelusik.eatery.exception.common.BadRequestException;

import java.util.List;

public class MismatchedMenuKeywordCountException extends BadRequestException {

    public MismatchedMenuKeywordCountException(List<String> menus, List<String> menuKeywords) {
        super(String.valueOf(menus) + menuKeywords);
    }
}
