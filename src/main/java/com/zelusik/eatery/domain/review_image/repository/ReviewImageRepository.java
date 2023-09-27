package com.zelusik.eatery.domain.review_image.repository;

import com.zelusik.eatery.domain.review_image.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImageRepository extends
        JpaRepository<ReviewImage, Long>,
        ReviewImageRepositoryQCustom {
}
