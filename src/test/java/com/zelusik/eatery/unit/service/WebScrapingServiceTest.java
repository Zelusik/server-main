package com.zelusik.eatery.unit.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.constant.place.DayOfWeek;
import com.zelusik.eatery.dto.place.PlaceScrapingOpeningHourDto;
import com.zelusik.eatery.dto.place.PlaceScrapingResponse;
import com.zelusik.eatery.service.WebScrapingService;
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

    @DisplayName("카카오 장소 아이디가 주어지고, 장소 정보를 스크래핑하면, 추출된 정보를 반환한다.")
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
        PlaceScrapingResponse result = sut.getPlaceScrapingInfo(kakaoPid);

        // then
        restServer.verify();
        assertThat(result).isNotNull();
        assertThat(result.getOpeningHours()).hasSize(3);
        assertThat(result.getClosingHours()).isNotEmpty();
        assertThat(result.getHomepageUrl()).isNull();
    }

    private static PlaceScrapingResponse createPlaceScrapingResponse() {
        return PlaceScrapingResponse.of(
                List.of(
                        PlaceScrapingOpeningHourDto.of(DayOfWeek.MON, LocalTime.of(12, 0), LocalTime.of(21, 0)),
                        PlaceScrapingOpeningHourDto.of(DayOfWeek.TUE, LocalTime.of(12, 0), LocalTime.of(21, 0)),
                        PlaceScrapingOpeningHourDto.of(DayOfWeek.WED, LocalTime.of(12, 0), LocalTime.of(21, 0))
                ),
                "목요일\n금요일\n토요일\n일",
                null
        );
    }
}