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
