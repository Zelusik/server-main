package com.zelusik.eatery.domain.recommended_review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class BatchUpdateRecommendedReviewsRequest {

    @Schema(description = "<p>갱신하고자 하는 추천 리뷰 목록." +
                          "<p>추천 리뷰로 설정하고자 하는 리뷰 세 개를 모두 보내야 한다.")
    @NotNull
    @Size(min = 3, max = 3)
    private List<RecommendedReviewRequest> recommendedReviews;

    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class RecommendedReviewRequest {

        @Schema(description = "추천 리뷰로 등록하고자 하는 리뷰의 id(PK)", example = "2")
        @NotNull
        private Long reviewId;

        @Schema(description = "추천 순위. 값은 1~3만 가능합니다.", example = "1")
        @NotNull
        @Min(1)
        @Max(3)
        private Short ranking;
    }
}
