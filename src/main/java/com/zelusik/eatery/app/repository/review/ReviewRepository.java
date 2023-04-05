package com.zelusik.eatery.app.repository.review;

import com.zelusik.eatery.app.domain.review.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByIdAndDeletedAtNull(Long reviewId);

    @EntityGraph(attributePaths = {"writer"})
    Slice<Review> findByPlace_IdAndDeletedAtNull(Long placeId, Pageable pageable);

    @EntityGraph(attributePaths = {"writer", "place"})
    Slice<Review> findByWriter_IdAndDeletedAtNull(Long writerId, Pageable pageable);

    @EntityGraph(attributePaths = {"writer", "place"})
    Slice<Review> findAllByDeletedAtNull(Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Review r SET r.deletedAt = CURRENT_TIMESTAMP WHERE r = :review")
    void softDelete(@Param("review") Review review);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Review r SET r.deletedAt = CURRENT_TIMESTAMP WHERE r IN (:reviews)")
    void softDeleteAll(@Param("reviews") List<Review> reviews);
}
