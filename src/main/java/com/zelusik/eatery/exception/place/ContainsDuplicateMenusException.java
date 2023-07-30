package com.zelusik.eatery.exception.place;

import com.zelusik.eatery.exception.common.BadRequestException;

import java.util.List;

public class ContainsDuplicateMenusException extends BadRequestException {
    public ContainsDuplicateMenusException(List<String> menus) {
        super("전달받은 메뉴 목록=" + menus);
    }
}
