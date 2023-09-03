package com.zelusik.eatery.dto.auth;

import java.time.LocalDateTime;

public record TokenDto(
        String accessToken,
        LocalDateTime accessTokenExpiresAt,
        String refreshToken,
        LocalDateTime refreshTokenExpiresAt
) {
}
