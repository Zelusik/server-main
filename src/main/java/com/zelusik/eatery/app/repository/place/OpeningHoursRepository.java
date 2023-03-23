package com.zelusik.eatery.app.repository.place;

import com.zelusik.eatery.app.domain.place.OpeningHours;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpeningHoursRepository extends JpaRepository<OpeningHours, Long> {
}
