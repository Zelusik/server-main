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

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ReviewImage ri SET ri.deletedAt=CURRENT_TIMESTAMP WHERE ri=:reviewImage")
    void softDelete(@Param("reviewImage") ReviewImage reviewImage);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ReviewImage ri SET ri.deletedAt=CURRENT_TIMESTAMP WHERE ri IN (:reviewImages)")
    void softDeleteAll(@Param("reviewImages") List<ReviewImage> reviewImages);
}
