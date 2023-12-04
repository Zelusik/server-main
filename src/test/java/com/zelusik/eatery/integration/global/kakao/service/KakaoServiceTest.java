package com.zelusik.eatery.integration.global.kakao.service;

import com.zelusik.eatery.global.kakao.dto.KakaoPlaceInfo;
import com.zelusik.eatery.global.kakao.service.KakaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static com.zelusik.eatery.domain.meeting_place.api.MeetingPlaceController.PAGE_SIZE_OF_SEARCHING_MEETING_PLACES;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Integration] Service - Kakao")
@ActiveProfiles("test")
@SpringBootTest(classes = {KakaoService.class, RestTemplate.class})
class KakaoServiceTest {

    @Autowired
    private KakaoService sut;

    @DisplayName("검색 키워드가 주어지고, 카카오에서 키워드로 장소들을 검색하면, 검색된 장소들이 반환된다.")
    @Test
    void givenKeyword_whenSearchingPlacesFromKakao_thenReturnSearchingResult() {
        // given
        String keyword = "서울";

        // when
        Slice<KakaoPlaceInfo> kakaoPlaceResponses = sut.searchKakaoPlacesByKeyword(keyword, Pageable.ofSize(PAGE_SIZE_OF_SEARCHING_MEETING_PLACES));

        // then
        assertThat(kakaoPlaceResponses.getContent()).isNotEmpty();
        assertThat(kakaoPlaceResponses.hasNext()).isTrue();
    }
}