package com.zelusik.eatery.dto.exception;

public record ValidationErrorDetails(
        Integer code,
        String field,
        String message
) {
}
