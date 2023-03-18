package com.zelusik.eatery.app.repository;

import com.zelusik.eatery.app.domain.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"writer"})
    Slice<Review> findByPlace_Id(Long placeId, Pageable pageable);
}
