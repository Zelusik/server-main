package com.zelusik.eatery.domain.report_review.dto;

import com.zelusik.eatery.domain.report_review.entity.ReportReview;
import com.zelusik.eatery.domain.review.dto.ReviewDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.zelusik.eatery.domain.review.constant.ReviewEmbedOption.PLACE;
import static com.zelusik.eatery.domain.review.constant.ReviewEmbedOption.WRITER;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReportReviewDto {

    private Long id;
    private Long reporterId;
    private ReviewDto review;
    private ReportReviewReasonOption reasonOption;
    private String reasonDetail;

    public static ReportReviewDto from(ReportReview entity) {
        return new ReportReviewDto(
                entity.getId(),
                entity.getReporter().getId(),
                ReviewDto.from(entity.getReview(), List.of(WRITER, PLACE)),
                entity.getReasonOption(),
                entity.getReasonDetail()
        );
    }
}

