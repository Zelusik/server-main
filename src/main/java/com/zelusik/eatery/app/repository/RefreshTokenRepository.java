package com.zelusik.eatery.app.repository;

import com.zelusik.eatery.app.dto.auth.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
