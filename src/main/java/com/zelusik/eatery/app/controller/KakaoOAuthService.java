package com.zelusik.eatery.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.app.dto.auth.KakaoOAuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@Service
public class KakaoOAuthService {

    private final ObjectMapper objectMapper;
    private final HttpRequestService httpRequestService;

    public KakaoOAuthService(HttpRequestService httpRequestService) {
        this.objectMapper = new ObjectMapper();
        this.httpRequestService = httpRequestService;
    }

    public KakaoOAuthUserInfo getUserInfo(String accessToken) {
        String requestUrl = "https://kapi.kakao.com/v2/user/me";

        // HTTP header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP request 보내기
        ResponseEntity<String> response = httpRequestService.sendHttpRequest(requestUrl, HttpMethod.GET, headers);

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
