package com.zelusik.eatery.domain.report_review.repository;

import com.zelusik.eatery.domain.location.entity.Location;
import com.zelusik.eatery.domain.report_review.entity.ReportReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportReviewRepository extends JpaRepository<ReportReview, Location>, ReportReviewRepositoryQCustom {
}
