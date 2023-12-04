package com.zelusik.eatery.global.exception.dto;

import java.util.List;

public record ValidationErrorResponse(
        Integer code,
        String message,
        List<ValidationErrorDetails> errors
) {
}
