package com.zelusik.eatery.domain.bookmark.exception;

import com.zelusik.eatery.global.common.exception.NotFoundException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class BookmarkNotFoundException extends NotFoundException {

    public BookmarkNotFoundException() {
        super(CustomExceptionType.BOOKMARK_NOT_FOUND);
    }
}
