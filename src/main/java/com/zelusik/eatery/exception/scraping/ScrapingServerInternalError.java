package com.zelusik.eatery.exception.scraping;

import com.zelusik.eatery.exception.common.InternalServerException;

public class ScrapingServerInternalError extends InternalServerException {

    public ScrapingServerInternalError(Throwable cause) {
        super(cause);
    }
}
