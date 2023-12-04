package com.zelusik.eatery.domain.report_review.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zelusik.eatery.domain.report_review.dto.ReportReviewDto;
import com.zelusik.eatery.domain.report_review.entity.ReportReview;
import com.zelusik.eatery.domain.report_review.exception.ReportReviewNotFoundByIdException;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.zelusik.eatery.domain.place.entity.QPlace.place;
import static com.zelusik.eatery.domain.report_review.entity.QReportReview.reportReview;
import static com.zelusik.eatery.domain.review.entity.QReview.review;

@RequiredArgsConstructor
public class ReportReviewRepositoryQCustomImpl implements ReportReviewRepositoryQCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public ReportReviewDto findDtoByReportReviewId(long reportReviewId) {
        ReportReview result = Optional.ofNullable(
                queryFactory
                        .select(reportReview)
                        .from(reportReview)
                        .join(reportReview.review, review).fetchJoin()
                        .join(review.place, place).fetchJoin()
                        .where(reportReview.id.eq(reportReviewId))
                        .fetchOne()
        ).orElseThrow(() -> new ReportReviewNotFoundByIdException(reportReviewId));

        return ReportReviewDto.from(result);
    }
}
