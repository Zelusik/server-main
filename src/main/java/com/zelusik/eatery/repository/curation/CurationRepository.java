package com.zelusik.eatery.repository.curation;

import com.zelusik.eatery.domain.curation.Curation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurationRepository extends JpaRepository<Curation, Long> {

    List<Curation> findAllByOrderByCreatedAtDesc();
}
