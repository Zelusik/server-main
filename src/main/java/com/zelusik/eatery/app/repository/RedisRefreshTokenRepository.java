package com.zelusik.eatery.app.repository;

import com.zelusik.eatery.app.dto.auth.RedisRefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RedisRefreshTokenRepository extends CrudRepository<RedisRefreshToken, String> {
}
