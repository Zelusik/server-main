package com.zelusik.eatery.repository.review;

import com.zelusik.eatery.domain.review.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryQCustom {

    @EntityGraph(attributePaths = {"writer", "place"})
    Optional<Review> findByIdAndDeletedAtNull(Long reviewId);

    @EntityGraph(attributePaths = {"writer", "place"})
    Slice<Review> findByWriter_IdAndDeletedAtNull(Long writerId, Pageable pageable);

    @EntityGraph(attributePaths = {"writer", "place"})
    Slice<Review> findAllByDeletedAtNull(Pageable pageable);
}
