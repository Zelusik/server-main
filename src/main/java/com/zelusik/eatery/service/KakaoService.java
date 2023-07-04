package com.zelusik.eatery.service;

import com.zelusik.eatery.dto.exception.ErrorResponse;
import com.zelusik.eatery.dto.kakao.KakaoOAuthUserResponse;
import com.zelusik.eatery.dto.kakao.KakaoPlaceResponse;
import com.zelusik.eatery.exception.kakao.KakaoServerException;
import com.zelusik.eatery.exception.kakao.KakaoTokenValidateException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class KakaoService {

    private final RestTemplate restTemplate;

    @Value("${kakao.rest-api.key}")
    private String apiKey;

    public KakaoOAuthUserResponse getUserInfo(String accessToken) {
        URI requestUri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com/v2/user/me")
                .build().toUri();

        // HTTP header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP request 보내기
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(requestUri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            Integer errorCode = getErrorDetails(ex).code();
            String errorMessage = getErrorDetails(ex).message();
            if (errorCode == 401) {
                throw new KakaoTokenValidateException(errorCode, errorMessage, ex);
            }
            throw new KakaoServerException(ex.getStatusCode(), errorCode, errorMessage, ex);
        } catch (Exception ex) {
            ErrorResponse errorDetails = getErrorDetails(ex);
            throw new KakaoServerException(HttpStatus.INTERNAL_SERVER_ERROR, errorDetails.code(), errorDetails.message(), ex);
        }

        Map<String, Object> attributes = new JSONObject(response.getBody()).toMap();
        return KakaoOAuthUserResponse.from(attributes);
    }

    /**
     * Kakao api를 활용해 키워드에 해당하는 지하철역, 관광명소, 학교를 검색한다.
     *
     * @param keyword  검색 키워드
     * @param pageable paging 정보
     * @return 검색 결과
     * @see <a href="https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword">Kakao developers - 키워드로 장소 검색하기</a>
     */
    @SuppressWarnings("unchecked")
    public Slice<KakaoPlaceResponse> searchKakaoPlacesByKeyword(String keyword, Pageable pageable) {
        URI requestUri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com/v2/local/search/keyword.json")
                .queryParam("page", pageable.getPageNumber() + 1)
                .queryParam("size", pageable.getPageSize())
                .queryParam("category_group_code", "SW8,AT4,SC4")
                .queryParam("query", keyword)
                .encode(StandardCharsets.UTF_8)
                .build().toUri();

        // Set header
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "KakaoAK " + apiKey);

        // Send http request
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(requestUri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        } catch (Exception ex) {
            ErrorResponse errorDetails = getErrorDetails(ex);
            throw new KakaoServerException(HttpStatus.INTERNAL_SERVER_ERROR, errorDetails.code(), errorDetails.message(), ex);
        }

        Map<String, Object> attributes = new JSONObject(response.getBody()).toMap();

        Map<String, Object> metadata = (Map<String, Object>) attributes.get("meta");
        boolean isEnd = Boolean.parseBoolean(String.valueOf(metadata.get("is_end")));

        List<Map<String, Object>> documents = (List<Map<String, Object>>) attributes.get("documents");
        List<KakaoPlaceResponse> content = documents.stream()
                .map(KakaoPlaceResponse::from)
                .toList();

        return new SliceImpl<>(content, pageable, !isEnd);
    }

    private ErrorResponse getErrorDetails(Exception ex) {
        int errorCode = 0;
        String errorMessage = ex.getMessage();
        if (errorMessage != null) {
            errorMessage = errorMessage.replace("\"", "");
            errorCode = parseErrorCode(errorMessage);
        }
        return new ErrorResponse(errorCode, errorMessage);
    }

    private int parseErrorCode(String errorMessage) {
        if (!errorMessage.contains("code:")) {
            return 0;
        }
        int errorCodeStartIdx = errorMessage.indexOf("code:-");
        int errorCodeEndIdx = errorMessage.indexOf("}");
        return Integer.parseInt(errorMessage.substring(errorCodeStartIdx + 6, errorCodeEndIdx));
    }
}
