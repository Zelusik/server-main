package com.zelusik.eatery.global.exception.dto;

public record ValidationErrorDetails(
        Integer code,
        String field,
        String message
) {
}
