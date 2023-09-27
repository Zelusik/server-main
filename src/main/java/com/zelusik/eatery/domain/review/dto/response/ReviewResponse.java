package com.zelusik.eatery.domain.review.dto.response;

import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review_image_menu_tag.entity.MenuTagPoint;
import com.zelusik.eatery.domain.member.dto.response.MemberResponse;
import com.zelusik.eatery.domain.place.dto.response.PlaceResponse;
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
public class ReviewResponse {

    @Schema(description = "리뷰 id(PK)", example = "1")
    private Long id;

    @Schema(description = "리뷰를 작성한 회원 정보")
    private MemberResponse writer;

    @Schema(description = "장소 정보")
    private PlaceResponse place;

    @Schema(description = "리뷰 키워드 목록", example = "[\"신선한 재료\", \"왁자지껄한\"]")
    private List<String> keywords;

    @Schema(description = "내용", example = "미래에 제가 살 곳은 여기로 정했습니다. 고기를 주문하면 ...")
    private String content;

    @Schema(description = "리뷰에 첨부된 이미지 파일 목록")
    private List<ReviewImageResponse> images;

    public static ReviewResponse from(ReviewDto reviewDto) {
        return new ReviewResponse(
                reviewDto.getId(),
                MemberResponse.from(reviewDto.getWriter()),
                PlaceResponse.from(reviewDto.getPlace()),
                reviewDto.getKeywords().stream()
                        .map(ReviewKeywordValue::getContent)
                        .toList(),
                reviewDto.getContent(),
                reviewDto.getReviewImageDtos().stream()
                        .map(ReviewImageResponse::from)
                        .toList()
        );
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class ReviewImageResponse {

        @Schema(description = "이미지 url", example = "https://review-image-url")
        private String url;

        @Schema(description = "썸네일 이미지 url", example = "https//review-thumbnail-image-url")
        private String thumbnailUrl;

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
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class ReviewImageMenuTagResponse {

        @Schema(description = "메뉴 이름", example = "떡볶이")
        private String content;

        private MenuTagPoint point;

        private static ReviewImageMenuTagResponse from(ReviewImageMenuTagDto dto) {
            return new ReviewImageMenuTagResponse(dto.getContent(), dto.getPoint());
        }
    }
}
