package com.zelusik.eatery.domain.opening_hour.repository;

import com.zelusik.eatery.domain.opening_hour.entity.OpeningHour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpeningHourRepository extends JpaRepository<OpeningHour, Long> {
}
