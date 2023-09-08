package com.zelusik.eatery.dto.place.request;

import com.zelusik.eatery.constant.FoodCategoryValue;
import com.zelusik.eatery.constant.place.DayOfWeek;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Setter
@Getter
public class FindNearPlacesFilteringConditionRequest {

    @Schema(description = "음식 카테고리 필터링 항목", example = "KOREAN")
    private FoodCategoryValue foodCategory;

    @Schema(description = "(필터링 조건) 요일 목록", example = "[\"MON\", \"TUE\", \"WED\"]")
    private List<DayOfWeek> daysOfWeek;

    @Schema(description = """
            <p>(필터링 조건) 선호하는 분위기. 가능한 값은 다음과 같다.</p>
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
            example = "WITH_ALCOHOL")
    private ReviewKeywordValue preferredVibe;

    @Schema(description = "저장한 장소만 조회할지에 대한 여부",
            example = "false",
            defaultValue = "false")
    private Boolean onlyMarkedPlaces;
}
