package com.zelusik.eatery.dto.recommended_review.response;

import com.zelusik.eatery.constant.FoodCategoryValue;
import com.zelusik.eatery.domain.place.Address;
import com.zelusik.eatery.dto.place.PlaceDto;
import com.zelusik.eatery.dto.recommended_review.RecommendedReviewDto;
import com.zelusik.eatery.dto.review.ReviewDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FindMyRecommendedReviewsResponse {
    private List<RecommendedReviewResponse> recommendedReviews;

    public static FindMyRecommendedReviewsResponse from(List<RecommendedReviewDto> recommendedReviewDtos) {
        return new FindMyRecommendedReviewsResponse(
                recommendedReviewDtos.stream()
                        .map(RecommendedReviewResponse::from)
                        .toList()
        );
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class RecommendedReviewResponse {

        @Schema(description = "PK of recommended review", example = "19")
        private Long id;

        @Schema(description = "추천 리뷰 정보")
        private ReviewResponse review;

        @Schema(description = "순위", example = "1")
        private Short ranking;

        private static RecommendedReviewResponse from(RecommendedReviewDto recommendedReviewDto) {
            return new RecommendedReviewResponse(
                    recommendedReviewDto.getId(),
                    ReviewResponse.from(recommendedReviewDto.getReview()),
                    recommendedReviewDto.getRanking()
            );
        }

        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @Getter
        private static class ReviewResponse {

            @Schema(description = "PK of review", example = "2")
            private Long id;

            @Schema(description = "장소 정보")
            private PlaceResponse place;

            private static ReviewResponse from(ReviewDto reviewDto) {
                return new ReviewResponse(
                        reviewDto.getId(),
                        PlaceResponse.from(reviewDto.getPlace())
                );
            }

            @AllArgsConstructor(access = AccessLevel.PRIVATE)
            @NoArgsConstructor(access = AccessLevel.PRIVATE)
            @Getter
            private static class PlaceResponse {

                @Schema(description = "장소의 id(PK)", example = "1")
                private Long id;

                @Schema(description = "이름", example = "연남토마 본점")
                private String name;

                @Schema(description = "음식 카테고리", example = "한식")
                private String category;

                @Schema(description = "주소")
                private Address address;

                @Schema(description = "장소에 대한 북마크 여부", example = "false")
                private Boolean isMarked;

                private static PlaceResponse from(PlaceDto dto) {
                    return new PlaceResponse(
                            dto.getId(),
                            dto.getName(),
                            FoodCategoryValue.valueOfFirstCategory(dto.getCategory().getFirstCategory()).getName(),
                            dto.getAddress(),
                            dto.getIsMarked()
                    );
                }
            }
        }
    }
}
