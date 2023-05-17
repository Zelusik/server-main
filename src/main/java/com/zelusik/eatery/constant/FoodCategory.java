package com.zelusik.eatery.constant;

import com.zelusik.eatery.exception.place.NotAcceptableFoodCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Schema(description = "<p>음식 카테고리 대분류." +
        "<p>목록은 다음과 같다." +
        "<ul>" +
        "<li>KOREAN - 한식" +
        "<li>JAPANESE - 일식" +
        "<li>CHINESE - 중식" +
        "<li>WESTERN - 양식" +
        "<li>MEET - 고기/구이" +
        "<li>CHICKEN - 치킨" +
        "<li>STREET - 분식" +
        "<li>FAST_FOOD - 패스트푸드" +
        "<li>DESERT - 디저트" +
        "<li>ASIAN - 아시안푸드" +
        "<li>SANDWICH - 샌드위치" +
        "<li>FUSION_WORLD - 퓨전/세계" +
        "<li>BUFFET - 뷔페" +
        "<li>BAR - 술집" +
        "</ul>",
        example = "KOREAN")
@AllArgsConstructor
@Getter
public enum FoodCategory {

    KOREAN("한식", new String[]{"한식", "샤브샤브"}),
    JAPANESE("일식", new String[]{"일식"}),
    CHINESE("중식", new String[]{"중식"}),
    WESTERN("양식", new String[]{"양식"}),
    CHICKEN("치킨", new String[]{"치킨"}),
    STREET("분식", new String[]{"분식"}),
    MEET("고기/구이", new String[]{"육류,고기"}),
    FAST_FOOD("패스트푸드", new String[]{"패스트푸드"}),
    DESERT("디저트", new String[]{"카페", "간식"}),
    ASIAN("아시안푸드", new String[]{"아시아음식"}),
    SANDWICH("샌드위치", new String[]{"샌드위치", "샐러드"}),
    FUSION_WORLD("퓨전/세계", new String[]{"퓨전요리"}),
    BUFFET("뷔페", new String[]{"패밀리레스토랑", "뷔페"}),
    BAR("술집", new String[]{"술집"});

    private final String name;
    private final String[] matchingKakaoCategories;

    public static FoodCategory valueOfDescription(String description) {
        String trimmedDescription = description.replace(" ", "");

        return Arrays.stream(values())
                .filter(value -> trimmedDescription.equals(
                        value.getName().replace(" ", "")
                )).findFirst()
                .orElseThrow(NotAcceptableFoodCategory::new);
    }
}
