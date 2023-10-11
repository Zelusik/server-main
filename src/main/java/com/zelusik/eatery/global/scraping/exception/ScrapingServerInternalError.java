package com.zelusik.eatery.global.scraping.exception;

import com.zelusik.eatery.global.common.exception.InternalServerException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class ScrapingServerInternalError extends InternalServerException {

    public ScrapingServerInternalError(Throwable cause) {
        super(CustomExceptionType.SCRAPING_SERVER_UNAVAILABLE, cause);
    }
}
