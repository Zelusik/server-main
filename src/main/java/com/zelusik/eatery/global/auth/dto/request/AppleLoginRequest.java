package com.zelusik.eatery.global.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class AppleLoginRequest {

    @Schema(description = "Apple에서 받은 identity token")
    @NotBlank
    private String identityToken;

    @Schema(description = "회원 이름", example = "임가비")
    private String name;
}
