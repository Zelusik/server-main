package com.zelusik.eatery.dto.place.response;

import com.zelusik.eatery.constant.place.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.Point;
import com.zelusik.eatery.dto.kakao.KakaoPlaceResponse;
import com.zelusik.eatery.dto.location.LocationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[DTO] 약속 장소 응답 데이터 테스트")
class MeetingPlaceResponseTest {

    @DisplayName("LocationDto 객체가 주어지면, MeetingPlaceResponse 객체로 변환한다.")
    @Test
    void givenLocationDtoObject_whenInstantiating_thenConvertMeetingPlaceResponseObject() {
        // given
        LocationDto locationDto = LocationDto.of(877L, "경기도", "수원시", null, new Point("37.263476", "127.028646"));

        // when
        MeetingPlaceResponse result = MeetingPlaceResponse.from(locationDto);

        // then
        assertThat(result.getName()).isEqualTo("수원시");
        assertThat(result.getSido()).isEqualTo("경기도");
        assertThat(result.getSgg()).isEqualTo("수원시");
        assertThat(result.getEmd()).isEqualTo(null);
        assertThat(result.getPoint().getLat()).isEqualTo("37.263476");
        assertThat(result.getPoint().getLng()).isEqualTo("127.028646");
    }

    @DisplayName("KakaoPlaceResponse 객체가 주어지면, MeetingPlaceResponse 객체로 변환한다.")
    @Test
    void givenKakaoPlaceResponseObject_whenInstantiating_thenConvertMeetingPlaceResponseObject() {
        // given
        KakaoPlaceResponse kakaoPlaceResponse = KakaoPlaceResponse.of(
                "카카오프렌즈 코엑스점",
                418,
                "http://place.map.kakao.com/26338954",
                "가정,생활 > 문구,사무용품 > 디자인문구 > 카카오프렌즈",
                "서울 강남구 삼성동 159",
                "서울 강남구 영동대로 513",
                "26338954",
                "02-6002-1880",
                KakaoCategoryGroupCode.AT4,
                "127.05902969025047",
                "37.51207412593136"
        );

        // when
        MeetingPlaceResponse result = MeetingPlaceResponse.from(kakaoPlaceResponse);

        // then
        assertThat(result.getName()).isEqualTo("카카오프렌즈 코엑스점");
        assertThat(result.getSido()).isEqualTo("서울");
        assertThat(result.getSgg()).isEqualTo("강남구");
        assertThat(result.getEmd()).isEqualTo("삼성동");
        assertThat(result.getPoint().getLat()).isEqualTo("37.51207412593136");
        assertThat(result.getPoint().getLng()).isEqualTo("127.05902969025047");
    }
}