package com.zelusik.eatery.constant;

import com.zelusik.eatery.exception.place.NotAcceptableFoodCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Schema(
        description = "<p>음식 카테고리 대분류. 목록은 다음과 같다." +
                "<ul>" +
                "<li>KOREAN(\"한식\")" +
                "<li>JAPANESE(\"일식\")" +
                "<li>CHINESE(\"중식\")" +
                "<li>WESTERN(\"양식\")" +
                "<li>MEET(\"고기/구이\")" +
                "<li>CHICKEN(\"치킨\")" +
                "<li>STREET(\"분식\")" +
                "<li>FAST_FOOD(\"패스트푸드\")" +
                "<li>DESERT(\"디저트\")" +
                "<li>ASIAN(\"아시안푸드\")" +
                "<li>SANDWICH(\"샌드위치\")" +
                "<li>FUSION_WORLD(\"퓨전/세계\")"
)
@AllArgsConstructor
@Getter
public enum FoodCategory {

    KOREAN("한식"),
    JAPANESE("일식"),
    CHINESE("중식"),
    WESTERN("양식"),
    MEET("고기/구이"),
    CHICKEN("치킨"),
    STREET("분식"),
    FAST_FOOD("패스트푸드"),
    DESERT("디저트"),
    ASIAN("아시안푸드"),
    SANDWICH("샌드위치"),
    FUSION_WORLD("퓨전/세계"),
    ;

    private final String description;

    public static FoodCategory valueOfDescription(String description) {
        String trimmedDescription = description.replace(" ", "");

        return Arrays.stream(values())
                .filter(value -> trimmedDescription.equals(
                        value.getDescription().replace(" ", "")
                )).findFirst()
                .orElseThrow(NotAcceptableFoodCategory::new);
    }
}
