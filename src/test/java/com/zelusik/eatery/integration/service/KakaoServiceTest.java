package com.zelusik.eatery.integration.service;

import com.zelusik.eatery.dto.kakao.KakaoPlaceResponse;
import com.zelusik.eatery.service.HttpRequestService;
import com.zelusik.eatery.service.KakaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static com.zelusik.eatery.constant.ConstantUtil.PAGE_SIZE_OF_SEARCHING_MEETING_PLACES;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Integration] Kakao Service")
@ActiveProfiles("test")
@SpringBootTest(classes = {KakaoService.class, ObjectMapper.class, HttpRequestService.class, RestTemplate.class})
class KakaoServiceTest {

    @Autowired
    private KakaoService sut;

    @DisplayName("검색 키워드가 주어지고, 카카오에서 키워드로 장소들을 검색하면, 검색된 장소들이 반환된다.")
    @Test
    void givenKeyword_whenSearchingPlacesFromKakao_thenReturnSearchingResult() {
        // given

        // when
        Slice<KakaoPlaceResponse> kakaoPlaceResponses = sut.searchKakaoPlacesByKeyword("서울", PageRequest.of(1, PAGE_SIZE_OF_SEARCHING_MEETING_PLACES));

        // then
        assertThat(kakaoPlaceResponses).isNotEmpty();
        assertThat(kakaoPlaceResponses.hasNext()).isTrue();
    }
}