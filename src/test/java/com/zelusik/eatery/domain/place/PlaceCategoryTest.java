package com.zelusik.eatery.domain.place;

import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class PlaceCategoryTest {


    @DisplayName("카테고리 이름이 주어지고 PlaceCategory 객체를 생성하면 세 개의 값으로 구성된 카테고리 정보가 구성된다.")
    @MethodSource("categoryInfos")
    @ParameterizedTest(name = "[{index}] {0}, {1} => 시/도: {2}, 시/군/구: {3}, 지번주소: {4}, 도로명주소: {5}")
    void givenCategoryName_whenCreatePlaceCategory_thenStructureCategoryInfos(
            String categoryName,
            String firstCategory,
            String secondCategory,
            String thirdCategory
    ) {
        // given

        // when
        PlaceCategory placeCategory = PlaceCategory.of(categoryName);

        // then
        assertThat(placeCategory.getFirstCategory()).isEqualTo(firstCategory);
        assertThat(placeCategory.getSecondCategory()).isEqualTo(secondCategory);
        assertThat(placeCategory.getThirdCategory()).isEqualTo(thirdCategory);
    }

    public static Stream<Arguments> categoryInfos() {
        return Stream.of(
                arguments("음식점 > 양식", "양식", null, null),
                arguments("음식점 > 패밀리레스토랑", "패밀리레스토랑", null, null),
                arguments("음식점 > 샤브샤브", "샤브샤브", null, null),
                arguments("음식점 > 양식 > 이탈리안", "양식", "이탈리안", null),
                arguments("음식점 > 한식 > 냉면", "한식", "냉면", null),
                arguments("음식점 > 일식 > 돈까스,우동", "일식", "돈까스,우동", null),
                arguments("음식점 > 간식 > 제과,베이커리", "간식", "제과,베이커리", null),
                arguments("음식점 > 한식 > 육류,고기 > 갈비", "한식", "육류,고기", "갈비"),
                arguments("음식점 > 카페 > 테마카페 > 디저트카페", "카페", "테마카페", "디저트카페"),
                arguments("음식점 > 한식 > 육류,고기 > 닭요리 > 삼계탕", "한식", "육류,고기", "닭요리")
        );
    }
}