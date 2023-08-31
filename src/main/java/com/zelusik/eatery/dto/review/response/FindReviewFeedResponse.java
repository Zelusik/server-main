package com.zelusik.eatery.dto.review.response;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.dto.member.response.MemberResponse;
import com.zelusik.eatery.dto.place.response.PlaceCompactResponse;
import com.zelusik.eatery.dto.review.ReviewDto;
import com.zelusik.eatery.dto.review.ReviewImageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FindReviewFeedResponse {

    @Schema(description = "리뷰 id(PK)", example = "1")
    private Long id;

    @Schema(description = "리뷰를 작성한 회원 정보")
    private MemberResponse writer;

    @Schema(description = "리뷰가 작성된 가게 정보")
    private PlaceCompactResponse place;

    @Schema(description = "키워드 목록", example = "[\"신선한 재료\", \"최고의 맛\"]")
    private List<String> keywords;

    @Schema(description = "내용", example = "미래에 제가 살 곳은 여기로 정했습니다. 고기를 주문하면 ...")
    private String content;

    @Schema(description = "리뷰 대표 이미지")
    private ReviewImageResponse reviewImage;

    @Schema(description = "리뷰 작성 시간", example = "2023-09-01T08:01:58.253461")
    private LocalDateTime createdAt;

    public static FindReviewFeedResponse from(ReviewDto dto) {
        return new FindReviewFeedResponse(
                dto.getId(),
                MemberResponse.from(dto.getWriter()),
                PlaceCompactResponse.from(dto.getPlace()),
                dto.getKeywords().stream()
                        .map(ReviewKeywordValue::getDescription)
                        .toList(),
                dto.getContent(),
                new ReviewImageResponse(dto.getReviewImageDtos().get(0).getUrl(), dto.getReviewImageDtos().get(0).getThumbnailUrl()),
                dto.getCreatedAt()
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

        private static ReviewImageResponse from(ReviewImageDto dto) {
            return new ReviewImageResponse(
                    dto.getUrl(),
                    dto.getThumbnailUrl()
            );
        }
    }
}
