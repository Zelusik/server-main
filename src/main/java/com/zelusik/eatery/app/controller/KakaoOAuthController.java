package com.zelusik.eatery.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.app.dto.auth.KakaoOAuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class KakaoOAuthController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpRequestController httpRequestController;

    // accessToken은 Bearer ~~ 형식이어야 함.
    public KakaoOAuthUserInfo getUserInfo(String accessToken) {
        String requestUrl = "https://kapi.kakao.com/v2/user/me";

        // HTTP header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, accessToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP request 보내기
        ResponseEntity<String> response = httpRequestController.sendHttpRequest(requestUrl, headers);

        // Response의 body에서 user info 추출
        Map<String, Object> attributes;
        try {
            attributes = objectMapper.readValue(response.getBody(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            attributes = Collections.emptyMap();
        }
        return KakaoOAuthUserInfo.from(attributes);
    }
}
