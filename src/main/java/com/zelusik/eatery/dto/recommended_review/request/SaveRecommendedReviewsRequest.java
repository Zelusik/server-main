package com.zelusik.eatery.dto.recommended_review.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SaveRecommendedReviewsRequest {

    @Schema(description = "추천 리뷰로 등록하고자 하는 리뷰의 id(PK)", example = "2")
    @NotNull
    private Long reviewId;

    @Schema(description = "추천 순위. 값은 1~3만 가능합니다.", example = "1")
    @NotNull
    @Min(1)
    @Max(3)
    private Short ranking;
}
