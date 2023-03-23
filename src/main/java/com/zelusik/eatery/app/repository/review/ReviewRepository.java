package com.zelusik.eatery.app.repository.review;

import com.zelusik.eatery.app.domain.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByIdAndDeletedAtNull(Long reviewId);

    @EntityGraph(attributePaths = {"writer"})
    Slice<Review> findByPlace_IdAndDeletedAtNull(Long placeId, Pageable pageable);

    @EntityGraph(attributePaths = {"writer", "place"})
    Slice<Review> findByWriter_IdAndDeletedAtNull(Long writerId, Pageable pageable);
}
