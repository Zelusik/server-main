package com.zelusik.eatery.global.common.exception;

import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

public class AsyncInterruptedException extends InternalServerException {

    public AsyncInterruptedException(InterruptedException ex) {
        super(CustomExceptionType.ASYNC_INTERRUPTED, ex);
    }
}
