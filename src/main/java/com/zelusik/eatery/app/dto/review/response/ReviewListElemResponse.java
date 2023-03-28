package com.zelusik.eatery.app.dto.review.response;

import com.zelusik.eatery.app.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.app.dto.file.response.ImageResponse;
import com.zelusik.eatery.app.dto.review.ReviewDtoWithMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReviewListElemResponse {
    @Schema(description = "리뷰 id(PK)", example = "1")
    private Long id;

    @Schema(description = "리뷰를 작성한 회원의 id(PK)", example = "1")
    private Long writerId;

    @Schema(description = "리뷰 키워드 목록", example = "[\"신선한 재료\", \"왁자지껄한\"]")
    private List<String> keywords;

    @Schema(description = "내용", example = "미래에 제가 살 곳은 여기로 정했습니다. 고기를 주문하면 ...")
    private String content;

    @Schema(description = "리뷰에 첨부된 이미지 파일 목록")
    private List<ImageResponse> images;

    public static ReviewListElemResponse of(Long id, Long writerId, List<String> keywords, String content, List<ImageResponse> images) {
        return new ReviewListElemResponse(id, writerId, keywords, content, images);
    }

    public static ReviewListElemResponse from(ReviewDtoWithMember dto) {
        return of(
                dto.id(),
                dto.writerDto().getId(),
                dto.keywords().stream()
                        .map(ReviewKeywordValue::getDescription)
                        .toList(),
                dto.content(),
                dto.reviewFileDtos().stream()
                        .map(reviewFileDto -> ImageResponse.of(reviewFileDto.url(), reviewFileDto.thumbnailUrl()))
                        .toList()
        );
    }
}
