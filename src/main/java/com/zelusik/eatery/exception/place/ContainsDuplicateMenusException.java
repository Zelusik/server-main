package com.zelusik.eatery.exception.place;

import com.zelusik.eatery.exception.common.BadRequestException;

import java.util.List;

public class ContainsDuplicateMenusException extends BadRequestException {

    public ContainsDuplicateMenusException(Long placeId, List<String> menus) {
        super("placeId=" + placeId + ", 전달받은 메뉴 목록=" + menus);
    }

    public ContainsDuplicateMenusException(Long placeId, String menu) {
        super("placeId=" + placeId + ", 전달받은 메뉴=" + menu);
    }
}
