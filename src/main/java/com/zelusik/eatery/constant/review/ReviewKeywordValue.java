package com.zelusik.eatery.constant.review;

import com.zelusik.eatery.exception.review.NotAcceptableReviewKeyword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Schema(description = """ 
        <p>리뷰 키워드. 목록은 다음과 같다.</p>
        <p><strong>음식/가격 관련</strong></p>
        <ul>
           <li><code>FRESH</code>: 신선한 재료</li>
           <li><code>BEST_FLAVOR</code>: 최고의 맛</li>
           <li><code>BEST_MENU_COMBINATION</code>: 완벽한 메뉴 조합</li>
           <li><code>LOCAL_FLAVOR</code>: 현지 느낌이 가득한</li>
           <li><code>GOOD_PRICE</code>: 가성비가 좋은</li>
           <li><code>GENEROUS_PORTIONS</code>: 넉넉한 양</li>
        </ul>
        <p><strong>분위기 관련</strong></p>
        <ul>
           <li><code>WITH_ALCOHOL</code>: 술과 함께하기 좋은</li>
           <li><code>GOOD_FOR_DATE</code>: 데이트 하기에 좋은</li>
           <li><code>WITH_ELDERS</code>: 웃어른과 함께하기 좋은</li>
           <li><code>CAN_ALONE</code>: 혼밥 가능한</li>
           <li><code>PERFECT_FOR_GROUP_MEETING</code>: 단체 모임에 좋은</li>
           <li><code>WAITING</code>: 웨이팅 있는</li>
           <li><code>SILENT</code>: 조용조용한</li>
           <li><code>NOISY</code>: 왁자지껄한</li>
        </ul>
        """,
        example = "NOISY")
@AllArgsConstructor
@Getter
public enum ReviewKeywordValue {

    FRESH(ReviewKeywordType.FOOD_PRICE, "신선한 재료"),
    BEST_FLAVOR(ReviewKeywordType.FOOD_PRICE, "최고의 맛"),
    BEST_MENU_COMBINATION(ReviewKeywordType.FOOD_PRICE, "완벽한 메뉴 조합"),
    LOCAL_FLAVOR(ReviewKeywordType.FOOD_PRICE, "현지 느낌이 가득한"),
    GOOD_PRICE(ReviewKeywordType.FOOD_PRICE, "가성비가 좋은"),
    GENEROUS_PORTIONS(ReviewKeywordType.FOOD_PRICE, "넉넉한 양"),

    WITH_ALCOHOL(ReviewKeywordType.VIBE, "술과 함께하기 좋은"),
    GOOD_FOR_DATE(ReviewKeywordType.VIBE, "데이트 하기에 좋은"),
    WITH_ELDERS(ReviewKeywordType.VIBE, "웃어른과 함께하기 좋은"),
    CAN_ALONE(ReviewKeywordType.VIBE, "혼밥 가능한"),
    PERFECT_FOR_GROUP_MEETING(ReviewKeywordType.VIBE, "단체 모임에 좋은"),
    WAITING(ReviewKeywordType.VIBE, "웨이팅 있는"),
    SILENT(ReviewKeywordType.VIBE, "조용조용한"),
    NOISY(ReviewKeywordType.VIBE, "왁자지껄한"),
    ;

    private final ReviewKeywordType type;
    private final String description;

    public static ReviewKeywordValue valueOfDescription(String description) {
        String trimmedDescription = description.replace(" ", "");

        return Arrays.stream(values())
                .filter(value -> trimmedDescription.equals(
                        value.getDescription().replace(" ", "")
                )).findFirst()
                .orElseThrow(() -> new NotAcceptableReviewKeyword(description));
    }

    @AllArgsConstructor
    @Getter
    public enum ReviewKeywordType {

        FOOD_PRICE("음식/가격"),
        VIBE("분위기"),
        ;

        private final String description;
    }
}
