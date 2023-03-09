package com.zelusik.eatery.app.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpRequestService {

    public ResponseEntity<String> sendHttpRequest(String requestUrl, HttpMethod httpMethod, HttpHeaders headers) {
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        return new RestTemplate().exchange(
                requestUrl,
                httpMethod,
                kakaoUserInfoRequest,
                String.class
        );
    }
}
