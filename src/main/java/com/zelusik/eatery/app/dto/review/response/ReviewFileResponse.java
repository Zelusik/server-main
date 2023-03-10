package com.zelusik.eatery.app.dto.review.response;

import com.zelusik.eatery.app.dto.review.ReviewFileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReviewFileResponse {

    @Schema(description = "리뷰에 첨부된 이미지 파일의 id(PK)", example = "1")
    private Long id;

    @Schema(description = "이미지 파일 이름", example = "img.png")
    private String name;

    @Schema(description = "이미지 파일 url", example = "https://eatery-s3-bucket.s3.ap-northeast-2.amazonaws.com/review/e67b6749-4aec-4bce-9cf2-0f1d38feffdd.png")
    private String url;

    public static ReviewFileResponse of(Long id, String name, String url) {
        return new ReviewFileResponse(id, name, url);
    }

    public static ReviewFileResponse from(ReviewFileDto dto) {
        return of(dto.id(), dto.originalName(), dto.url());
    }
}
