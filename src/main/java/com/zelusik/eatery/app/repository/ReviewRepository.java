package com.zelusik.eatery.app.repository;

import com.zelusik.eatery.app.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
