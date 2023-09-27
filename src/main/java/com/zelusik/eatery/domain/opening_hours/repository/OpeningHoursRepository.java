package com.zelusik.eatery.domain.opening_hours.repository;

import com.zelusik.eatery.domain.opening_hours.entity.OpeningHours;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpeningHoursRepository extends JpaRepository<OpeningHours, Long> {
}
