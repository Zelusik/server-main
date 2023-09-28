package com.zelusik.eatery.unit.domain.place.converter;

import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.place.converter.ReviewKeywordValueConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.stream.Stream;

import static com.zelusik.eatery.domain.review.constant.ReviewKeywordValue.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Review keyword converter test")
@Import(ReviewKeywordValueConverter.class)
class ReviewKeywordValueConverterTest {

    private final ReviewKeywordValueConverter reviewKeywordValueConverter;

    public ReviewKeywordValueConverterTest() {
        this.reviewKeywordValueConverter = new ReviewKeywordValueConverter();
    }

    @DisplayName("Review keywords가 담긴 List가 주어지면 연결된 문자열로 변환하여 반환한다.")
    @MethodSource("keywordList")
    @ParameterizedTest(name = "[{index}] {0} => {1}")
    void givenKeywordList_whenConvert_thenReturnConcatenatedString(
            List<ReviewKeywordValue> reviewKeywordValues,
            String expectedResult
    ) {
        // given

        // when
        String actualResult = reviewKeywordValueConverter.convertToDatabaseColumn(reviewKeywordValues);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @DisplayName("문자열이 주어지면 review keywords가 담긴 List를 반환한다.")
    @MethodSource("keywordsDbData")
    @ParameterizedTest(name = "[{index}] {0} => {1}")
    void givenString_whenConvert_thenReturnKeywordList(
            String dbData,
            List<ReviewKeywordValue> expectedResult
    ) {
        // given

        // when
        List<ReviewKeywordValue> actualResult = reviewKeywordValueConverter.convertToEntityAttribute(dbData);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    static Stream<Arguments> keywordList() {
        return Stream.of(
                arguments(null, ""),
                arguments(List.of(), ""),
                arguments(List.of(FRESH), "FRESH"),
                arguments(List.of(FRESH, BEST_FLAVOR), "FRESH BEST_FLAVOR"),
                arguments(List.of(FRESH, BEST_FLAVOR, BEST_MENU_COMBINATION), "FRESH BEST_FLAVOR BEST_MENU_COMBINATION"),
                arguments(List.of(FRESH, BEST_FLAVOR, GOOD_PRICE), "FRESH BEST_FLAVOR GOOD_PRICE"),
                arguments(List.of(FRESH, BEST_FLAVOR, GOOD_PRICE, CAN_ALONE), "FRESH BEST_FLAVOR GOOD_PRICE CAN_ALONE"),
                arguments(List.of(FRESH, BEST_FLAVOR, GOOD_PRICE, CAN_ALONE, WAITING), "FRESH BEST_FLAVOR GOOD_PRICE CAN_ALONE WAITING")
        );
    }

    public static Stream<Arguments> keywordsDbData() {
        return Stream.of(
                arguments(null, List.of()),
                arguments("", List.of()),
                arguments("FRESH", List.of(FRESH)),
                arguments("FRESH BEST_FLAVOR", List.of(FRESH, BEST_FLAVOR)),
                arguments("FRESH BEST_FLAVOR BEST_MENU_COMBINATION", List.of(FRESH, BEST_FLAVOR, BEST_MENU_COMBINATION)),
                arguments("FRESH BEST_FLAVOR GOOD_PRICE", List.of(FRESH, BEST_FLAVOR, GOOD_PRICE)),
                arguments("FRESH BEST_FLAVOR GOOD_PRICE CAN_ALONE", List.of(FRESH, BEST_FLAVOR, GOOD_PRICE, CAN_ALONE)),
                arguments("FRESH BEST_FLAVOR GOOD_PRICE CAN_ALONE WAITING", List.of(FRESH, BEST_FLAVOR, GOOD_PRICE, CAN_ALONE, WAITING))
        );
    }
}