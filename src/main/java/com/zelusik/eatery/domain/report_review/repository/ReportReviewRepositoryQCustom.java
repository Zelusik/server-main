package com.zelusik.eatery.domain.report_review.repository;

import com.zelusik.eatery.domain.report_review.dto.ReportReviewDto;

public interface ReportReviewRepositoryQCustom {

    /**
     * 특정 리뷰 신고 내역을 단건으로 조회한다.
     *
     * @param reportReviewId 조회하고자 하는 리뷰 신고 PK
     * @return 조회된 리뷰 신고 내역의 dto
     */
    ReportReviewDto findDtoByReportReviewId(long reportReviewId);
}
