package com.zelusik.eatery.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.app.dto.place.PlaceScrapingInfo;
import com.zelusik.eatery.global.exception.ExceptionUtils;
import com.zelusik.eatery.global.exception.scraping.ScrapingServerInternalError;
import com.zelusik.eatery.global.log.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Transactional(readOnly = true)
@Service
public class WebScrapingService {

    private final ObjectMapper objectMapper;
    private final HttpRequestService httpRequestService;

    @Value("${web-scraping.server.url:127.0.0.1:5000}")
    private String scrapingServerUrl;

    public WebScrapingService(HttpRequestService httpRequestService) {
        this.objectMapper = new ObjectMapper();
        this.httpRequestService = httpRequestService;
    }

    /**
     * Web scraping server에서 장소의 추가 정보를 scarping한다.
     *
     * @param placeUrl 장소 정보를 읽어올 장소 상세 페이지 주소
     * @return Scraping해서 읽어온 추가 정보
     */
    public PlaceScrapingInfo getPlaceScrapingInfo(String placeUrl) {
        String requestUrl = scrapingServerUrl + "/api/scrap/places?page_url=" + placeUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        try {
            ResponseEntity<String> response = httpRequestService.sendHttpRequest(requestUrl, HttpMethod.GET, headers);
            Map<String, Object> responseMap = mapResponseEntityToStringObjectMap(response);

            return PlaceScrapingInfo.from(responseMap);
        } catch (Exception e) {
            throw new ScrapingServerInternalError(e);
        }
    }

    /**
     * ResponseEntity를 Map 데이터로 변환한다.
     *
     * @param responseEntity Map으로 변환하고자 하는 ResponseEntity 데이터
     * @return 변환된 Map 데이터
     */
    private Map<String, Object> mapResponseEntityToStringObjectMap(ResponseEntity<String> responseEntity) {
        Map<String, Object> attributes;
        try {
            attributes = objectMapper.readValue(responseEntity.getBody(), new TypeReference<>() {
            });
        } catch (JsonProcessingException ex) {
            log.info("[{}] !Catch Exception! WebScrapingService.mapResponseEntityToStringObjectMap() ex={}", LogUtils.getLogTraceId(), ExceptionUtils.getExceptionStackTrace(ex));
            attributes = Collections.emptyMap();
        }
        return attributes;
    }
}
