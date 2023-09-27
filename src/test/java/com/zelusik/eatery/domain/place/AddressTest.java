package com.zelusik.eatery.domain.place;

import com.zelusik.eatery.domain.place.entity.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class AddressTest {

    @DisplayName("주소 정보가 주어지고 Address entity를 생성하면 시/도, 시/군/구, 상세주소 단위로 주소 정보를 구성한다")
    @MethodSource("addressInfos")
    @ParameterizedTest(name = "[{index}] {0}, {1} => 시/도: {2}, 시/군/구: {3}, 지번주소: {4}, 도로명주소: {5}")
    void givenAddressInfos_whenCreateEntity_thenStructureAddressInfos(
            String fullLotNumberAddress,
            String fullRoadAddress,
            String sido,
            String sgg,
            String lotNumberAddress,
            String roadAddress
    ) {
        // given

        // when
        Address address = Address.of(fullLotNumberAddress, fullRoadAddress);

        // then
        assertThat(address.getSido()).isEqualTo(sido);
        assertThat(address.getSgg()).isEqualTo(sgg);
        assertThat(address.getLotNumberAddress()).isEqualTo(lotNumberAddress);
        assertThat(address.getRoadAddress()).isEqualTo(roadAddress);
    }

    static Stream<Arguments> addressInfos() {
        return Stream.of(
                arguments(null, "대전 중구 대종로480번길 15", "대전", "중구", null, "대종로480번길 15"),
                arguments("", "대전 중구 대종로480번길 15", "대전", "중구", null, "대종로480번길 15"),
                arguments("대전 중구 은행동 145-1", null, "대전", "중구", "은행동 145-1", null),
                arguments("대전 중구 은행동 145-1", "", "대전", "중구", "은행동 145-1", null),
                arguments("대전 중구 은행동 145-1", "대전 중구 대종로480번길 15", "대전", "중구", "은행동 145-1", "대종로480번길 15"),
                arguments("서울 중구 명동2가 25-36", "서울 중구 명동10길 29", "서울", "중구", "명동2가 25-36", "명동10길 29"),
                arguments("서울 강남구 논현동 115-10", "서울 강남구 학동로 305-3", "서울", "강남구", "논현동 115-10", "학동로 305-3"),
                arguments("부산 해운대구 중동 1225-1", "부산 해운대구 중동2로10번길 32-10", "부산", "해운대구", "중동 1225-1", "중동2로10번길 32-10"),
                arguments("제주특별자치도 서귀포시 색달동 2156-2", "제주특별자치도 서귀포시 일주서로 968-10", "제주특별자치도", "서귀포시", "색달동 2156-2", "일주서로 968-10"),
                arguments("강원 강릉시 구정면 어단리 1011-1", "강원 강릉시 구정면 현천길 7", "강원", "강릉시", "구정면 어단리 1011-1", "구정면 현천길 7"),
                arguments("충남 태안군 안면읍 정당리 1112-3", "충남 태안군 안면읍 안면대로 2356-2", "충남", "태안군", "안면읍 정당리 1112-3", "안면읍 안면대로 2356-2")
        );
    }
}