package com.zelusik.eatery.service;

import com.google.gson.Gson;
import com.zelusik.eatery.dto.place.PlaceScrapingInfo;
import com.zelusik.eatery.exception.scraping.ScrapingServerInternalError;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class WebScrapingService {

    private final HttpRequestService httpRequestService;
    private final Gson gson;

    @Value("${web-scraping.server.url:http://127.0.0.1:5000}")
    private String scrapingServerUrl;


    /**
     * Web scraping server에서 장소의 추가 정보를 scarping한다.
     *
     * @param placeUrl 장소 정보를 읽어올 장소 상세 페이지 주소
     * @return Scraping해서 읽어온 추가 정보
     * @throws ScrapingServerInternalError Web scraping 서버에서 에러가 발생한 경우
     */
    public PlaceScrapingInfo getPlaceScrapingInfo(String placeUrl) {
        String requestUrl = scrapingServerUrl + "/api/scrap/places?page_url=" + placeUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        ResponseEntity<String> response;
        try {
            response = httpRequestService.sendHttpRequest(requestUrl, HttpMethod.GET, headers);
        } catch (Exception ex) {
            throw new ScrapingServerInternalError(ex);
        }

        return gson.fromJson(response.getBody(), PlaceScrapingInfo.class);
    }
}
