package com.zelusik.eatery.domain.review.dto.response;

import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.review.dto.ReviewDto;
import com.zelusik.eatery.domain.review_image.dto.ReviewImageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FindReviewsWrittenByMeResponse {

    @Schema(description = "PK of review", example = "2")
    private Long id;

    @Schema(description = "장소 정보")
    private PlaceResponse place;

    @Schema(description = "리뷰 대표 이미지")
    private ReviewImageResponse reviewImage;

    public static FindReviewsWrittenByMeResponse from(ReviewDto reviewDto) {
        return new FindReviewsWrittenByMeResponse(
                reviewDto.getId(),
                PlaceResponse.from(reviewDto.getPlace()),
                ReviewImageResponse.from(reviewDto.getReviewImageDtos().get(0))
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
        private String url;

        @Schema(description = "썸네일 이미지 url", example = "https//review-thumbnail-image-url")
        private String thumbnailUrl;

        private static ReviewImageResponse from(ReviewImageDto dto) {
            return new ReviewImageResponse(
                    dto.getUrl(),
                    dto.getThumbnailUrl()
            );
        }
    }
}
