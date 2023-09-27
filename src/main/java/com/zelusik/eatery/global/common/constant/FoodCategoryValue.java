package com.zelusik.eatery.global.common.constant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Schema(description = "<p>음식 종류. 목록은 다음과 같다." +
                      "<ul>" +
                      "   <li><code>KOREAN</code> - 한식</li>" +
                      "   <li><code>JAPANESE</code> - 일식</li>" +
                      "   <li><code>CHINESE</code> - 중식</li>" +
                      "   <li><code>WESTERN</code> - 양식</li>" +
                      "   <li><code>MEAT</code> - 고기/구이</li>" +
                      "   <li><code>CHICKEN</code> - 치킨</li>" +
                      "   <li><code>STREET</code> - 분식</li>" +
                      "   <li><code>FAST_FOOD</code> - 패스트푸드</li>" +
                      "   <li><code>CAFE_DESSERT</code> - 카페/디저트</li>" +
                      "   <li><code>ASIAN</code> - 아시안푸드</li>" +
                      "   <li><code>SANDWICH</code> - 샌드위치</li>" +
                      "   <li><code>FUSION_WORLD</code> - 퓨전/세계</li>" +
                      "   <li><code>BUFFET</code> - 뷔페</li>" +
                      "   <li><code>BAR</code> - 술집</li>" +
                      "</ul>",
        example = "KOREAN")
@AllArgsConstructor
@Getter
public enum FoodCategoryValue {

    KOREAN("한식", List.of("한식", "샤브샤브")),
    JAPANESE("일식", List.of("일식")),
    CHINESE("중식", List.of("중식")),
    WESTERN("양식", List.of("양식")),
    CHICKEN("치킨", List.of("치킨")),
    STREET("분식", List.of("분식")),
    MEAT("고기/구이", List.of("육류,고기")),
    FAST_FOOD("패스트푸드", List.of("패스트푸드")),
    CAFE_DESSERT("카페/디저트", List.of("카페", "간식")),
    ASIAN("아시안푸드", List.of("아시아음식")),
    SANDWICH("샌드위치", List.of("샌드위치", "샐러드")),
    FUSION_WORLD("퓨전/세계", List.of("퓨전요리")),
    BUFFET("뷔페", List.of("패밀리레스토랑", "뷔페")),
    BAR("술집", List.of("술집"));

    private final String categoryName;
    private final List<String> matchingFirstCategories;

    public static FoodCategoryValue valueOfFirstCategory(String firstCategory) {
        return Arrays.stream(values())
                .filter(value -> value.getMatchingFirstCategories().contains(firstCategory))
                .findFirst()
                .orElseThrow();
    }
}
