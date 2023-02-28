package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.dto.auth.RedisRefreshToken;
import com.zelusik.eatery.app.repository.RedisRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RedisRefreshTokenService {

    private final RedisRefreshTokenRepository redisRefreshTokenRepository;

    /**
     * Redis에 refresh token을 저장한다.
     *
     * @param memberId redis key로 사용할 회원의 PK 값
     * @param token redis에 저장할 refresh token
     */
    @Transactional
    public void save(String token, Long memberId) {
        redisRefreshTokenRepository.save(RedisRefreshToken.of(token, memberId));
    }

    public RedisRefreshToken findByToken(String token) {
        return redisRefreshTokenRepository.findById(token)
                .orElseThrow();
    }
}
