package com.zelusik.eatery.domain.recommended_review.repository;

import com.zelusik.eatery.domain.location.entity.Location;
import com.zelusik.eatery.domain.recommended_review.entity.RecommendedReview;
import com.zelusik.eatery.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendedReviewRepository extends JpaRepository<RecommendedReview, Location>, RecommendedReviewRepositoryQCustom {

    void deleteAllByMember(Member member);
}
