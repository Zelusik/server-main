package com.zelusik.eatery.repository.location;

import com.zelusik.eatery.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends
        JpaRepository<Location, Long>,
        LocationRepositoryQCustom {
}
