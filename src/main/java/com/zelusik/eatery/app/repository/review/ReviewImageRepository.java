package com.zelusik.eatery.app.repository.review;

import com.zelusik.eatery.app.domain.review.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewImageRepository extends
        JpaRepository<ReviewImage, Long>,
        ReviewImageQuerydslRepository {
}
