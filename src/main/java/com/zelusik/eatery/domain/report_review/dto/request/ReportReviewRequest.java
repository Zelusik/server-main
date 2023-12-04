package com.zelusik.eatery.domain.report_review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReportReviewRequest {

    @Schema(description = "신고하고자 하는 리뷰의 id(PK)", example = "2")
    @NotNull
    private Long reviewId;

    @Schema(description = "신고 이유 선택(UNRELATED, ADVERTISING, SENSATIONAL, UNAUTHORIZED, PRIVACY, ETC 중 택 1)", example = "ETC")
    @NotNull
    private String reasonOption;

    @Schema(description = "신고 이유 상세", example = "제가 리뷰로 올린 사진을 도용하였어요.")
    @NotNull
    private String reasonDetail;
}