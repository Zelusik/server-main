package com.zelusik.eatery.app.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class KakaoLoginRequest {

    @Schema(description = "카카오 서버에서 받은 access Token", example = "4rzV6xwsGVjz8im0ZHAJJc9GISbC-8hiMszZcHtKCj1y6wAAAYaHOixY")
    @NotBlank
    private String kakaoAccessToken;
}
