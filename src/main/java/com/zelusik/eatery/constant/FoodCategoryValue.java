package com.zelusik.eatery.constant;

import com.zelusik.eatery.exception.place.NotAcceptableFoodCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Schema(description = "<p>음식 종류. 목록은 다음과 같다." +
        "<ul>" +
        "   <li><code>KOREAN</code> - 한식</li>" +
        "   <li><code>JAPANESE</code> - 일식</li>" +
        "   <li><code>CHINESE</code> - 중식</li>" +
        "   <li><code>WESTERN</code> - 양식</li>" +
        "   <li><code>MEET</code> - 고기/구이</li>" +
        "   <li><code>CHICKEN</code> - 치킨</li>" +
        "   <li><code>STREET</code> - 분식</li>" +
        "   <li><code>FAST_FOOD</code> - 패스트푸드</li>" +
        "   <li><code>CAFE_DESERT</code> - 카페/디저트</li>" +
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

    KOREAN("한식", new String[]{"한식", "샤브샤브"}),
    JAPANESE("일식", new String[]{"일식"}),
    CHINESE("중식", new String[]{"중식"}),
    WESTERN("양식", new String[]{"양식"}),
    CHICKEN("치킨", new String[]{"치킨"}),
    STREET("분식", new String[]{"분식"}),
    MEET("고기/구이", new String[]{"육류,고기"}),
    FAST_FOOD("패스트푸드", new String[]{"패스트푸드"}),
    CAFE_DESERT("카페/디저트", new String[]{"카페", "간식"}),
    ASIAN("아시안푸드", new String[]{"아시아음식"}),
    SANDWICH("샌드위치", new String[]{"샌드위치", "샐러드"}),
    FUSION_WORLD("퓨전/세계", new String[]{"퓨전요리"}),
    BUFFET("뷔페", new String[]{"패밀리레스토랑", "뷔페"}),
    BAR("술집", new String[]{"술집"});

    private final String name;
    private final String[] matchingFirstCategories;

    public static FoodCategoryValue valueOfDescription(String description) {
        String trimmedDescription = description.replace(" ", "");

        return Arrays.stream(values())
                .filter(value -> trimmedDescription.equals(
                        value.getName().replace(" ", "")
                )).findFirst()
                .orElseThrow(NotAcceptableFoodCategory::new);
    }
}
