package com.zelusik.eatery.global.common.exception;

import com.zelusik.eatery.global.exception.constant.CustomExceptionType;

import java.util.concurrent.ExecutionException;

public class AsyncExecutionException extends InternalServerException {

    public AsyncExecutionException(ExecutionException ex) {
        super(CustomExceptionType.ASYNC_EXECUTION, ex.getMessage(), ex);
    }
}
