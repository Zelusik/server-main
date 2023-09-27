package com.zelusik.eatery.global.auth.dto.response;

import com.zelusik.eatery.global.auth.dto.JwtTokenDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RefreshTokenResponse {

    @Schema(description = "Access token", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IlJPTEVfVVNFUiIsImxvZ2luVHlwZSI6IktBS0FPIiwiaWF0IjoxNjc3NDg0NzExLCJleHAiOjE2Nzc1Mjc5MTF9.eM2R_mMRqkPUsMmJN_vm2lAsIGownPJZ6Xu47K6ujrI")
    private String accessToken;

    @Schema(description = "Access token 만료 시각", example = "2023-02-28T17:13:55.473")
    private LocalDateTime accessTokenExpiresAt;

    @Schema(description = "Refresh token", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IlJPTEVfVVNFUiIsImxvZ2luVHlwZSI6IktBS0FPIiwiaWF0IjoxNjc3NDg0NzExLCJleHAiOjE2Nzg2OTQzMTF9.QCq8dj7yet9SKCzt9APu73yaM-_Fx7mowtkl5-bOd64")
    private String refreshToken;

    @Schema(description = "Refresh token 만료 시각", example = "2023-03-30T05:13:55.473")
    private LocalDateTime refreshTokenExpiresAt;

    public static RefreshTokenResponse from(JwtTokenDto jwtTokenDto) {
        return new RefreshTokenResponse(
                jwtTokenDto.accessToken(),
                jwtTokenDto.accessTokenExpiresAt(),
                jwtTokenDto.refreshToken(),
                jwtTokenDto.refreshTokenExpiresAt()
        );
    }
}
