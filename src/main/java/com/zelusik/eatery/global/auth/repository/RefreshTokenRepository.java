package com.zelusik.eatery.global.auth.repository;

import com.zelusik.eatery.global.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
