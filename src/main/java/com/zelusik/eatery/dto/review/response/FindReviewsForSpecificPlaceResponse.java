package com.zelusik.eatery.dto.review.response;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.dto.member.MemberDto;
import com.zelusik.eatery.dto.review.ReviewDto;
import com.zelusik.eatery.dto.review.ReviewImageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FindReviewsForSpecificPlaceResponse {

    @Schema(description = "리뷰 id(PK)", example = "1")
    private Long id;

    @Schema(description = "작성자 정보")
    private WriterResponse writer;

    @Schema(description = "리뷰 키워드 목록", example = "[\"신선한 재료\", \"왁자지껄한\"]")
    private List<String> keywords;

    @Schema(description = "내용", example = "미래에 제가 살 곳은 여기로 정했습니다. 고기를 주문하면 ...")
    private String content;

    @Schema(description = "리뷰에 첨부된 썸네일 이미지 url")
    private List<String> reviewThumbnailImageUrls;

    public static FindReviewsForSpecificPlaceResponse from(ReviewDto dto) {
        return new FindReviewsForSpecificPlaceResponse(
                dto.getId(),
                WriterResponse.from(dto.getWriter()),
                dto.getKeywords().stream()
                        .map(ReviewKeywordValue::getDescription)
                        .toList(),
                dto.getContent(),
                dto.getReviewImageDtos().stream()
                        .map(ReviewImageDto::getThumbnailUrl)
                        .toList()
        );
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class WriterResponse {

        @Schema(description = "PK of member", example = "1")
        private Long id;

        @Schema(description = "회원 프로필 썸네일 이미지 url", example = "https://member-profile-thumbnail-image-url")
        private String profileThumbnailImageUrl;

        @Schema(description = "닉네임", example = "우기")
        private String nickname;

        private static WriterResponse from(MemberDto dto) {
            return new WriterResponse(
                    dto.getId(),
                    dto.getProfileThumbnailImageUrl(),
                    dto.getNickname()
            );
        }
    }
}
