package com.zelusik.eatery.domain.report_review.dto.response;

import com.zelusik.eatery.domain.report_review.dto.ReportReviewDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PostReportReviewResponse {

    @Schema(description = "리뷰 신고의 id(PK)", example = "19")
    private Long id;

    @Schema(description = "리뷰를 신고한 회원의 id(PK)", example = "15")
    private Long reporterId;

    @Schema(description = "신고한 리뷰의 id(PK)", example = "2")
    private Long reviewId;

    @Schema(description = "신고 이유 옵션 전체 문장", example = "광고/홍보성 게시글임")
    private String reasonOption;

    @Schema(description = "신고 이유 상세", example = "제가 리뷰로 올린 사진을 도용하였어요.")
    private String reasonDetail;

    public static PostReportReviewResponse from(ReportReviewDto reportReviewDto) {
        return new PostReportReviewResponse(
                reportReviewDto.getId(),
                reportReviewDto.getReporterId(),
                reportReviewDto.getReview().getId(),
                reportReviewDto.getReasonOption().getFullSentence(),
                reportReviewDto.getReasonDetail()
        );
    }
}