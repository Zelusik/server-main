package com.zelusik.eatery.global.auth.service;

import com.zelusik.eatery.global.auth.repository.RefreshTokenRepository;
import com.zelusik.eatery.global.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class JwtTokenQueryService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * <p>
     * Refresh token의 유효성을 검사한다.
     * <p>
     * Refresh token이 유효하지 않은 값인 경우, refresh token이 만료된 경우가 유효하지 않은 경우이다.
     *
     * @param refreshToken 유효성을 검사할 refreshToken
     * @return refresh token의 유효성 검사 결과
     */
    public boolean validateOfRefreshToken(String refreshToken) {
        try {
            jwtTokenProvider.validateToken(refreshToken);
        } catch (Exception ex) {
            return false;
        }
        return refreshTokenRepository.existsById(refreshToken);
    }
}
