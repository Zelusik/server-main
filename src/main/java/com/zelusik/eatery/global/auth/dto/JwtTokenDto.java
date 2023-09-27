package com.zelusik.eatery.global.auth.dto;

import java.time.LocalDateTime;

public record JwtTokenDto(
        String accessToken,
        LocalDateTime accessTokenExpiresAt,
        String refreshToken,
        LocalDateTime refreshTokenExpiresAt
) {
}
