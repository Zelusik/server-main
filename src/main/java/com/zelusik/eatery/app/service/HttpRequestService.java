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

    /**
     * 외부 server에 HTTP request를 보낸다.
     *
     * @param requestUrl 요청을 보낼 외부 서버의 url
     * @param httpMethod 보낼 요청의 HTTP method
     * @param headers    header 정보
     * @return 요청에 대한 응답 데이터
     */
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
