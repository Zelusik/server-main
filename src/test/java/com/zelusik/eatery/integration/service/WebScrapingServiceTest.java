package com.zelusik.eatery.integration.service;

import com.zelusik.eatery.dto.place.PlaceScrapingResponse;
import com.zelusik.eatery.service.WebScrapingService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("실제 API 호출 결과 관찰용이므로 평상시엔 비활성화. 외부 API는 정상적으로 동작한다고 가정하는 것이 일반적임")
@DisplayName("[Integration] Scraping server와 통신하는 business logic")
@ActiveProfiles("test")
@SpringBootTest(classes = {WebScrapingService.class, RestTemplate.class})
class WebScrapingServiceTest {

    @Autowired
    private WebScrapingService sut;

    @DisplayName("카카오 장소 아이디가 주어지고, 장소 정보를 스크래핑하면, 추출된 정보를 반환한다.")
    @Test
    void given_when_then() {
        // given
        String kakaoPid = "1879186093"; // 경기도 수원시 일호선의 kakao place id

        // when
        PlaceScrapingResponse response = sut.getPlaceScrapingInfo(kakaoPid);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getOpeningHours()).hasSize(6);  // 월,화,수,목,금,토
        assertThat(response.getClosingHours()).isEqualTo("일요일");
        assertThat(response.getHomepageUrl()).isEqualTo("https://www.instagram.com/1st_station_/");
    }
}