package com.zelusik.eatery.dto.kakao;

import com.zelusik.eatery.constant.place.KakaoCategoryGroupCode;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[DTO] Kakao 장소 정보 응답 데이터 테스트")
class KakaoPlaceResponseTest {

    @DisplayName("Kakao 장소 응답이 주어지면, KakaoPlaceResponse 객체로 변환한다.")
    @Test
    void givenKakaoPlaceResponse_whenInstantiating_thenConvertKakaoPlaceResponseObject() {
        // given
        String response = """
                {
                    "place_name": "카카오프렌즈 코엑스점",
                    "distance": "418",
                    "place_url": "http://place.map.kakao.com/26338954",
                    "category_name": "가정,생활 > 문구,사무용품 > 디자인문구 > 카카오프렌즈",
                    "address_name": "서울 강남구 삼성동 159",
                    "road_address_name": "서울 강남구 영동대로 513",
                    "id": "26338954",
                    "phone": "02-6002-1880",
                    "category_group_code": "AT4",
                    "category_group_name": "관광명소",
                    "x": "127.05902969025047",
                    "y": "37.51207412593136"
                }
                """;
        Map<String, Object> attributes = new JSONObject(response).toMap();

        // when
        KakaoPlaceResponse result = KakaoPlaceResponse.from(attributes);

        // then
        assertThat(result.getPlaceName()).isEqualTo("카카오프렌즈 코엑스점");
        assertThat(result.getDistance()).isEqualTo(418);
        assertThat(result.getPlaceUrl()).isEqualTo("http://place.map.kakao.com/26338954");
        assertThat(result.getCategoryName()).isEqualTo("가정,생활 > 문구,사무용품 > 디자인문구 > 카카오프렌즈");
        assertThat(result.getAddressName()).isEqualTo("서울 강남구 삼성동 159");
        assertThat(result.getRoadAddressName()).isEqualTo("서울 강남구 영동대로 513");
        assertThat(result.getId()).isEqualTo("26338954");
        assertThat(result.getPhone()).isEqualTo("02-6002-1880");
        assertThat(result.getCategoryGroupCode()).isEqualTo(KakaoCategoryGroupCode.AT4);
        assertThat(result.getX()).isEqualTo("127.05902969025047");
        assertThat(result.getY()).isEqualTo("37.51207412593136");
    }
}