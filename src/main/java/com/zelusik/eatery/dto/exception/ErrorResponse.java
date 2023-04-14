package com.zelusik.eatery.dto.exception;

public record ErrorResponse(
        Integer code,
        String message
) {
}
