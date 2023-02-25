package com.zelusik.eatery.app.dto.exception;

public record ErrorResponse(
        Integer code,
        String message
) {
}
