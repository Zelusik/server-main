package com.zelusik.eatery.domain.report_review.service;

import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.member.service.MemberQueryService;
import com.zelusik.eatery.domain.report_review.dto.ReportReviewDto;
import com.zelusik.eatery.domain.report_review.dto.ReportReviewReasonOption;
import com.zelusik.eatery.domain.report_review.entity.ReportReview;
import com.zelusik.eatery.domain.report_review.repository.ReportReviewRepository;
import com.zelusik.eatery.domain.review.entity.Review;
import com.zelusik.eatery.domain.review.service.ReviewQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ReportReviewCommandService {
    private final MemberQueryService memberQueryService;
    private final ReviewQueryService reviewQueryService;
    private final ReportReviewRepository reportReviewRepository;

    /**
     * 리뷰를 신고한다.
     *
     * @param reporterId   PK of login member
     * @param reviewId     신고하고자 하는 리뷰의 PK
     * @param reasonOption 신고 이유 옵션(UNRELATED, ADVERTISING, SENSATIONAL, UNAUTHORIZED, PRIVACY, ETC)
     * @param reasonDetail 신고 이유 상세
     * @return 리뷰 신고 dto
     */
    public ReportReviewDto reportReview(long reporterId, long reviewId, String reasonOption, String reasonDetail) {
        Member reporter = memberQueryService.getById(reporterId);
        Review review = reviewQueryService.getById(reviewId);
        ReportReviewReasonOption reportReviewReasonOption = ReportReviewReasonOption.valueOf(reasonOption);

        ReportReview reportReview = ReportReview.create(reporter, review, reportReviewReasonOption, reasonDetail);
        reportReviewRepository.save(reportReview);

        return ReportReviewDto.from(reportReview);
    }
}
