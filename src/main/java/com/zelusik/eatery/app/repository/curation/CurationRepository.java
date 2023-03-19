package com.zelusik.eatery.app.repository.curation;

import com.zelusik.eatery.app.domain.curation.Curation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurationRepository extends JpaRepository<Curation, Long> {
}
