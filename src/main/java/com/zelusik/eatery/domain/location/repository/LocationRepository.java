package com.zelusik.eatery.domain.location.repository;

import com.zelusik.eatery.domain.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends
        JpaRepository<Location, Long>,
        LocationRepositoryQCustom {
}
