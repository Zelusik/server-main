package com.zelusik.eatery.global.exception.dto;

public record ErrorResponse(
        Integer code,
        String message
) {
}
