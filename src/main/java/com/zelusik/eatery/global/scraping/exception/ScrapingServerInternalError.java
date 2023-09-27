package com.zelusik.eatery.global.scraping.exception;

import com.zelusik.eatery.global.common.exception.InternalServerException;

public class ScrapingServerInternalError extends InternalServerException {

    public ScrapingServerInternalError(Throwable cause) {
        super(cause);
    }
}
