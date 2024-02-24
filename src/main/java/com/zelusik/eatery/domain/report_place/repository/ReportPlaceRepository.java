package com.zelusik.eatery.domain.report_place.repository;

import com.zelusik.eatery.domain.report_place.entity.ReportPlace;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportPlaceRepository extends JpaRepository<ReportPlace, Long> {

    @EntityGraph(attributePaths = {"reporter", "place"})
    @Override
    Optional<ReportPlace> findById(Long id);
}
