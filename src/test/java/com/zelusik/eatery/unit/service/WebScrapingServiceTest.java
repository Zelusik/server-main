package com.zelusik.eatery.unit.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.domain.place.constant.DayOfWeek;
import com.zelusik.eatery.domain.place.dto.PlaceScrapingOpeningHourDto;
import com.zelusik.eatery.domain.place.dto.PlaceScrapingInfo;
import com.zelusik.eatery.global.scraping.service.WebScrapingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("[Unit] Scraping server와 통신하는 business logic")
@ActiveProfiles("test")
@AutoConfigureWebClient(registerRestTemplate = true)
@RestClientTest(WebScrapingService.class)
class WebScrapingServiceTest {

    private final WebScrapingService sut;
    private final MockRestServiceServer restServer;
    private final ObjectMapper mapper;

    @Value("${web-scraping.server.url}")
    private String scrapingServerUrl;

    @Autowired
    public WebScrapingServiceTest(WebScrapingService sut, MockRestServiceServer restServer, ObjectMapper mapper) {
        this.sut = sut;
        this.restServer = restServer;
        this.mapper = mapper;
    }

    @DisplayName("장소의 고유 id가 주어지고, 장소 정보를 스크래핑하면, 추출된 정보를 반환한다.")
    @Test
    void givenKakaoPid_whenScrapingPlaceInfo_thenReturnPlaceScarpingInfo() throws Exception {
        // given
        String kakaoPid = "12345";
        URI requestUri = UriComponentsBuilder.fromUriString(scrapingServerUrl + "/places/scraping")
                .queryParam("kakaoPid", kakaoPid)
                .encode(StandardCharsets.UTF_8)
                .build().toUri();
        restServer.expect(requestTo(requestUri))
                .andRespond(withSuccess(
                        mapper.writeValueAsString(createPlaceScrapingResponse()),
                        MediaType.APPLICATION_JSON
                ));

        // when
        PlaceScrapingInfo result = sut.getPlaceScrapingInfo(kakaoPid);

        // then
        restServer.verify();
        assertThat(result).isNotNull();
        assertThat(result.getOpeningHours()).hasSize(3);
        assertThat(result.getClosingHours()).isNotEmpty();
        assertThat(result.getHomepageUrl()).isNull();
    }

    @DisplayName("장소의 고유 id가 주어지고, 해당 장소에 대한 메뉴 목록을 추출하면, 추출된 정보를 반환한다.")
    @Test
    void givenKakaoPid_whenScrapPlaceMenuList_thenReturnPlaceMenuList() throws Exception {
        // given
        String kakaoPid = "12345";
        URI requestUri = UriComponentsBuilder.fromUriString(scrapingServerUrl + "/places/scraping/menus")
                .queryParam("kakaoPid", kakaoPid)
                .encode(StandardCharsets.UTF_8)
                .build().toUri();
        List<String> menus = List.of("돈까스", "라면", "김밥");
        restServer.expect(requestTo(requestUri))
                .andRespond(withSuccess(
                        mapper.writeValueAsString(createMenuListResponse(menus)),
                        MediaType.APPLICATION_JSON
                ));

        // when
        List<String> result = sut.scrapMenuList(kakaoPid);

        // then
        restServer.verify();
        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .hasSize(3);
    }

    private static PlaceScrapingInfo createPlaceScrapingResponse() {
        return PlaceScrapingInfo.of(
                List.of(
                        PlaceScrapingOpeningHourDto.of(DayOfWeek.MON, LocalTime.of(12, 0), LocalTime.of(21, 0)),
                        PlaceScrapingOpeningHourDto.of(DayOfWeek.TUE, LocalTime.of(12, 0), LocalTime.of(21, 0)),
                        PlaceScrapingOpeningHourDto.of(DayOfWeek.WED, LocalTime.of(12, 0), LocalTime.of(21, 0))
                ),
                "목요일\n금요일\n토요일\n일",
                null
        );
    }

    private static WebScrapingService.MenuListResponseFromScrapingServer createMenuListResponse(List<String> menus) {
        return new WebScrapingService.MenuListResponseFromScrapingServer(menus);
    }
}