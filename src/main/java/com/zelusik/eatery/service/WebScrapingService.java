package com.zelusik.eatery.service;

import com.zelusik.eatery.dto.place.PlaceScrapingResponse;
import com.zelusik.eatery.exception.scraping.ScrapingServerInternalError;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class WebScrapingService {

    private final RestTemplate restTemplate;

    @Value("${web-scraping.server.url}")
    private String scrapingServerUrl;


    /**
     * Web scraping server에서 장소의 추가 정보를 scarping한다.
     *
     * @param kakaoPid 장소 정보를 읽어올 장소의 고유 id 값
     * @return Scraping해서 읽어온 추가 정보
     * @throws ScrapingServerInternalError Web scraping 서버에서 에러가 발생한 경우
     */
    public PlaceScrapingResponse getPlaceScrapingInfo(String kakaoPid) {
        URI requestUri = UriComponentsBuilder.fromUriString("/places/scraping")
                .queryParam("kakaoPid", kakaoPid)
                .build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(requestUri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        } catch (Exception ex) {
            throw new ScrapingServerInternalError(ex);
        }

        Map<String, Object> attributes = new JSONObject(response.getBody()).toMap();
        return PlaceScrapingResponse.from(attributes);
    }
}
