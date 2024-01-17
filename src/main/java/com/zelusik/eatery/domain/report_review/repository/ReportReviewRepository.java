package com.zelusik.eatery.domain.report_review.repository;

import com.zelusik.eatery.domain.location.entity.Location;
import com.zelusik.eatery.domain.report_review.entity.ReportReview;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportReviewRepository extends JpaRepository<ReportReview, Location>, ReportReviewRepositoryQCustom {
    @EntityGraph(attributePaths = {"review", "place"})
    Optional<ReportReview> findById(Long id);
}
