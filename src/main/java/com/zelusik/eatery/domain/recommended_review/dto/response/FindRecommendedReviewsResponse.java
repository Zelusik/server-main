package com.zelusik.eatery.domain.recommended_review.dto.response;

import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.review_image_menu_tag.entity.MenuTagPoint;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.recommended_review.dto.RecommendedReviewDto;
import com.zelusik.eatery.domain.review.dto.ReviewDto;
import com.zelusik.eatery.domain.review_image.dto.ReviewImageDto;
import com.zelusik.eatery.domain.review_image_menu_tag.dto.ReviewImageMenuTagDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FindRecommendedReviewsResponse {

    private List<RecommendedReviewResponse> recommendedReviews;

    public static FindRecommendedReviewsResponse from(List<RecommendedReviewDto> recommendedReviewDtos) {
        return new FindRecommendedReviewsResponse(
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

            @Schema(description = "리뷰 이미지 파일 목록")
            private List<ReviewImageResponse> images;

            private static ReviewResponse from(ReviewDto reviewDto) {
                return new ReviewResponse(
                        reviewDto.getId(),
                        PlaceResponse.from(reviewDto.getPlace()),
                        reviewDto.getReviewImageDtos().stream()
                                .map(ReviewImageResponse::from)
                                .toList()
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
                            FoodCategoryValue.valueOfFirstCategory(dto.getCategory().getFirstCategory()).getCategoryName(),
                            dto.getAddress(),
                            dto.getIsMarked()
                    );
                }
            }

            @AllArgsConstructor(access = AccessLevel.PRIVATE)
            @NoArgsConstructor(access = AccessLevel.PRIVATE)
            @Getter
            private static class ReviewImageResponse {

                @Schema(description = "이미지 url", example = "https://review-image-url")
                private String imageUrl;

                @Schema(description = "썸네일 이미지 url", example = "https//review-thumbnail-image-url")
                private String thumbnailImageUrl;

                @Schema(description = "이미지에 생성된 메뉴 태그 목록")
                private List<ReviewImageMenuTagResponse> menuTags;

                private static ReviewImageResponse from(ReviewImageDto dto) {
                    return new ReviewImageResponse(
                            dto.getUrl(),
                            dto.getThumbnailUrl(),
                            Optional.ofNullable(dto.getMenuTags())
                                    .map(menuTags -> menuTags.stream()
                                            .map(ReviewImageMenuTagResponse::from)
                                            .toList())
                                    .orElse(List.of())
                    );
                }

                @AllArgsConstructor(access = AccessLevel.PRIVATE)
                @NoArgsConstructor(access = AccessLevel.PRIVATE)
                @Getter
                private static class ReviewImageMenuTagResponse {

                    @Schema(description = "메뉴 이름", example = "떡볶이")
                    private String content;

                    @Schema(description = "메뉴 태그 좌표")
                    private MenuTagPoint point;

                    private static ReviewImageMenuTagResponse from(ReviewImageMenuTagDto dto) {
                        return new ReviewImageMenuTagResponse(dto.getContent(), dto.getPoint());
                    }
                }
            }
        }
    }
}