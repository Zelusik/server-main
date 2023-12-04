package com.zelusik.eatery.domain.report_review.service;

import com.zelusik.eatery.domain.report_review.dto.ReportReviewDto;
import com.zelusik.eatery.domain.report_review.repository.ReportReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReportReviewQueryService {

    private final ReportReviewRepository reportReviewRepository;

    /**
     * reportReviewId에 해당하는 리뷰 신고 내역을 조회한다.
     *
     * @param reportReviewId 조회하고자 하는 리뷰 신고 내역의 PK
     * @return 조회된 리뷰 신고 내역의 dto
     */
    public ReportReviewDto findDtoByReportReviewId(Long reportReviewId) {
        return reportReviewRepository.findDtoByReportReviewId(reportReviewId);
    }
}
