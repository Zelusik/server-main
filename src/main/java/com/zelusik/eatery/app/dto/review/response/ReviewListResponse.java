package com.zelusik.eatery.app.dto.review.response;

import com.zelusik.eatery.app.domain.constant.ReviewKeyword;
import com.zelusik.eatery.app.dto.place.response.PlaceResponse;
import com.zelusik.eatery.app.dto.review.ReviewDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReviewListResponse {
    @Schema(description = "리뷰 id(PK)", example = "1")
    private Long id;

    @Schema(description = "리뷰를 작성한 회원의 id(PK)", example = "1")
    private Long writerId;

    @Schema(description = "리뷰 키워드 목록", example = "[\"FRESH\", \"NOISY\"]")
    private List<ReviewKeyword> keywords;

    @Schema(description = "내용", example = "미래에 제가 살 곳은 여기로 정했습니다. 고기를 주문하면 ...")
    private String content;

    @Schema(description = "리뷰에 첨부된 이미지 파일 목록")
    private List<ReviewFileResponse> reviewFiles;

    public static ReviewListResponse of(Long id, Long writerId, List<ReviewKeyword> keywords, String content, List<ReviewFileResponse> reviewFiles) {
        return new ReviewListResponse(id, writerId, keywords, content, reviewFiles);
    }

    public static ReviewListResponse from(ReviewDto dto) {
        return of(
                dto.id(),
                dto.writerDto().id(),
                dto.keywords(),
                dto.content(),
                dto.reviewFileDtos().stream()
                        .map(ReviewFileResponse::from)
                        .toList()
        );
    }
}
