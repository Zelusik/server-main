package com.zelusik.eatery.repository.redis;

import com.zelusik.eatery.dto.redis.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
