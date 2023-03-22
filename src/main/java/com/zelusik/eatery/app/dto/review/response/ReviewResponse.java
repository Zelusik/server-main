package com.zelusik.eatery.app.dto.review.response;

import com.zelusik.eatery.app.constant.review.ReviewKeyword;
import com.zelusik.eatery.app.dto.member.response.MemberResponse;
import com.zelusik.eatery.app.dto.place.response.PlaceResponse;
import com.zelusik.eatery.app.dto.review.ReviewDtoWithMemberAndPlace;
import com.zelusik.eatery.app.dto.review.ReviewFileDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

    @Schema(description = "리뷰에 첨부된 이미지 파일 목록", example = "[\"https://eatery-s3-bucket.s3.ap-northeast-2.amazonaws.com/review/0950af0e-3950-4596-bba2-4fee11e4938a.jpg\"]")
    private List<String> reviewFiles;

    public static ReviewResponse of(Long id, MemberResponse writer, PlaceResponse place, List<String> keywords, String content, List<String> reviewFiles) {
        return new ReviewResponse(id, writer, place, keywords, content, reviewFiles);
    }

    public static ReviewResponse from(ReviewDtoWithMemberAndPlace reviewDtoWithMemberAndPlace) {
        return new ReviewResponse(
                reviewDtoWithMemberAndPlace.id(),
                MemberResponse.from(reviewDtoWithMemberAndPlace.writerDto()),
                PlaceResponse.from(reviewDtoWithMemberAndPlace.placeDto()),
                reviewDtoWithMemberAndPlace.keywords().stream()
                        .map(ReviewKeyword::getDescription)
                        .toList(),
                reviewDtoWithMemberAndPlace.content(),
                reviewDtoWithMemberAndPlace.reviewFileDtos().stream()
                        .map(ReviewFileDto::url)
                        .toList()
        );
    }
}
