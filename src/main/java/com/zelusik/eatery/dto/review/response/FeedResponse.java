package com.zelusik.eatery.dto.review.response;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.dto.file.response.ImageResponse;
import com.zelusik.eatery.dto.member.response.MemberResponse;
import com.zelusik.eatery.dto.place.response.PlaceCompactResponse;
import com.zelusik.eatery.dto.review.ReviewDtoWithMemberAndPlace;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FeedResponse {

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

    @Schema(description = "리뷰에 첨부된 이미지 파일 목록", example = "[\"https://eatery-s3-bucket.s3.ap-northeast-2.amazonaws.com/review/0950af0e-3950-4596-bba2-4fee11e4938a.jpg\"]")
    private List<ImageResponse> images;

    public static FeedResponse of(Long id, MemberResponse writer, PlaceCompactResponse place, List<String> keywords, String content, List<ImageResponse> images) {
        return new FeedResponse(id, writer, place, keywords, content, images);
    }

    public static FeedResponse from(ReviewDtoWithMemberAndPlace dto) {
        return of(
                dto.getId(),
                MemberResponse.from(dto.getWriterDto()),
                PlaceCompactResponse.from(dto.getPlaceDtoWithMarkedStatus()),
                dto.getKeywords().stream()
                        .map(ReviewKeywordValue::getDescription)
                        .toList(),
                dto.getContent(),
                dto.getReviewImageDtos().stream()
                        .map(reviewImageDto -> ImageResponse.of(reviewImageDto.getUrl(), reviewImageDto.getThumbnailUrl()))
                        .toList()
        );
    }
}
