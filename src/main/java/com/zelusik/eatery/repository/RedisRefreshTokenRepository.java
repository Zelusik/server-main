package com.zelusik.eatery.repository;

import com.zelusik.eatery.dto.auth.RedisRefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RedisRefreshTokenRepository extends CrudRepository<RedisRefreshToken, String> {
}
