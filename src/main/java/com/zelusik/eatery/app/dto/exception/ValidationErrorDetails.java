package com.zelusik.eatery.app.dto.exception;

public record ValidationErrorDetails(
        Integer code,
        String field,
        String message
) {
}
