package com.zelusik.eatery.app.dto.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenValidateResponse {

    @Schema(description = "유효성 여부. 유효한 토큰이라면 true", example = "true")
    private Boolean isValid;
}
