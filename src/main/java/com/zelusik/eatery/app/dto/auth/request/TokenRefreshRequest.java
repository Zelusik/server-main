package com.zelusik.eatery.app.dto.auth.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class TokenRefreshRequest {

    @NotBlank
    private String refreshToken;
}
