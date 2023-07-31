package com.zelusik.eatery.dto.review.response;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.dto.member.response.MemberResponse;
import com.zelusik.eatery.dto.place.response.PlaceResponse;
import com.zelusik.eatery.dto.review.ReviewDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static ReviewResponse of(Long id, MemberResponse writer, PlaceResponse place, List<String> keywords, String content, List<ReviewImageResponse> images) {
        return new ReviewResponse(id, writer, place, keywords, content, images);
    }

    public static ReviewResponse from(ReviewDto reviewDto) {
        return of(
                reviewDto.getId(),
                MemberResponse.from(reviewDto.getWriter()),
                PlaceResponse.from(reviewDto.getPlace()),
                reviewDto.getKeywords().stream()
                        .map(ReviewKeywordValue::getDescription)
                        .toList(),
                reviewDto.getContent(),
                reviewDto.getReviewImageDtos().stream()
                        .map(ReviewImageResponse::from)
                        .toList()
        );
    }
}
