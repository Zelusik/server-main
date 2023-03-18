package com.zelusik.eatery.app.dto.review.response;

import com.zelusik.eatery.app.constant.review.ReviewKeyword;
import com.zelusik.eatery.app.dto.review.ReviewDtoWithMemberAndPlace;
import com.zelusik.eatery.app.dto.review.ReviewFileDto;
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

    @Schema(description = "리뷰 키워드 목록", example = "[\"신선한 재료\", \"왁자지껄한\"]")
    private List<String> keywords;

    @Schema(description = "내용", example = "미래에 제가 살 곳은 여기로 정했습니다. 고기를 주문하면 ...")
    private String content;

    @Schema(description = "리뷰에 첨부된 이미지 파일 목록")
    private List<String> reviewFiles;

    public static ReviewListResponse of(Long id, Long writerId, List<String> keywords, String content, List<String> reviewFiles) {
        return new ReviewListResponse(id, writerId, keywords, content, reviewFiles);
    }

    public static ReviewListResponse from(ReviewDtoWithMemberAndPlace dto) {
        return of(
                dto.id(),
                dto.writerDto().id(),
                dto.keywords().stream()
                        .map(ReviewKeyword::getDescription)
                        .toList(),
                dto.content(),
                dto.reviewFileDtos().stream()
                        .map(ReviewFileDto::url)
                        .toList()
        );
    }
}
