package com.zelusik.eatery.app.constant.place;

import com.zelusik.eatery.global.exception.place.NotAcceptablePlaceSearchKeyword;
import com.zelusik.eatery.global.exception.review.NotAcceptableReviewKeyword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Schema(
        description = "<p>가게 검색 키워드. 목록은 다음과 같다." +
                "<p>소개팅, 웃어른과, 혼밥, 단체, 맛집탐방, 조용한, 신나는",
        example = "EXCITING"
)
@AllArgsConstructor
@Getter
public enum PlaceSearchKeyword {

    BLIND_DATE("소개팅"),
    WITH_ELDERS("웃어른과"),
    ALONE("혼밥"),
    GROUP("단체"),
    FOOD_TOUR("맛집탐방"),
    SILENT("조용한"),
    EXCITING("신나는"),
    ;

    private final String description;

    public static PlaceSearchKeyword valueOfDescription(String description) {
        return Arrays.stream(values())
                .filter(value -> description.equals(value.getDescription()))
                .findFirst()
                .orElseThrow(NotAcceptablePlaceSearchKeyword::new);
    }
}
