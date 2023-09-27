package com.zelusik.eatery.global.scraping.service;

import com.zelusik.eatery.domain.place.dto.PlaceScrapingInfo;
import com.zelusik.eatery.global.scraping.exception.ScrapingServerInternalError;
import lombok.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
    public PlaceScrapingInfo getPlaceScrapingInfo(String kakaoPid) {
        URI requestUri = UriComponentsBuilder.fromUriString(scrapingServerUrl + "/places/scraping")
                .queryParam("kakaoPid", kakaoPid)
                .encode(StandardCharsets.UTF_8)
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
        return PlaceScrapingInfo.from(attributes);
    }

    /**
     * 장소 상세 페이지에서 메뉴 목록을 추출한다.
     *
     * @param kakaoPid 메뉴 데이터를 추출할 장소의 고유 id
     * @return 추출된 메뉴 목록
     */
    @NonNull
    public List<String> scrapMenuList(@NonNull String kakaoPid) {
        URI requestUri = UriComponentsBuilder.fromUriString(scrapingServerUrl + "/places/scraping/menus")
                .queryParam("kakaoPid", kakaoPid)
                .encode(StandardCharsets.UTF_8)
                .build().toUri();

        MenuListResponseFromScrapingServer result = restTemplate.getForObject(requestUri, MenuListResponseFromScrapingServer.class);
        if (result == null || result.getMenus() == null || result.getMenus().isEmpty()) {
            return List.of();
        }

        return result.getMenus();
    }

    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class MenuListResponseFromScrapingServer {
        private List<String> menus;
    }
}
