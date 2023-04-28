package com.zelusik.eatery.service;

import com.zelusik.eatery.constant.member.LoginType;
import com.zelusik.eatery.dto.auth.RedisRefreshToken;
import com.zelusik.eatery.dto.auth.response.TokenResponse;
import com.zelusik.eatery.repository.RedisRefreshTokenRepository;
import com.zelusik.eatery.exception.auth.TokenValidateException;
import com.zelusik.eatery.security.JwtTokenInfoDto;
import com.zelusik.eatery.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class JwtTokenService {

    private final RedisRefreshTokenRepository redisRefreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Access token과 refresh token을 생성한다.
     * 생성된 refresh token은 redis에 저장한다.
     * 이후, 생성된 access token과 refresh token 정보를 반환한다.
     *
     * @param memberId  jwt token을 생성하고자 하는 회원의 id
     * @param loginType jwt token을 생성하고자 하는 회원의 login type
     * @return 생성된 access token과 refresh token 정보가 담긴 <code>TokenResponse</code> 객체
     */
    @Transactional
    public TokenResponse create(Long memberId, LoginType loginType) {
        JwtTokenInfoDto accessTokenInfo = jwtTokenProvider.createAccessToken(memberId, loginType);
        JwtTokenInfoDto refreshTokenInfo = jwtTokenProvider.createRefreshToken(memberId, loginType);
        redisRefreshTokenRepository.save(RedisRefreshToken.of(refreshTokenInfo.token(), memberId));

        return TokenResponse.of(
                accessTokenInfo.token(), accessTokenInfo.expiresAt(),
                refreshTokenInfo.token(), refreshTokenInfo.expiresAt()
        );
    }

    /**
     * Refresh token을 전달받아 갱신한다.
     * 갱신 과정에서 기존 refresh token은 redis에서 삭제되고,
     * 새로운 access token과 refresh token이 생성된다. 새롭게 생성된 refresh token은 redis에 저장된다.
     *
     * @param oldRefreshToken 기존 발급받은 refresh token
     * @return 새롭게 생성된 access token과 refresh token 정보가 담긴 <code>TokenResponse</code> 객체
     * @throws TokenValidateException 유효하지 않은 token인 경우
     */
    @Transactional
    public TokenResponse refresh(String oldRefreshToken) {
        jwtTokenProvider.validateToken(oldRefreshToken);

        RedisRefreshToken oldRedisRefreshToken = redisRefreshTokenRepository.findById(oldRefreshToken)
                .orElseThrow(TokenValidateException::new);
        redisRefreshTokenRepository.delete(oldRedisRefreshToken);

        return create(
                oldRedisRefreshToken.getMemberId(),
                jwtTokenProvider.getLoginType(oldRefreshToken)
        );
    }

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
        return redisRefreshTokenRepository.existsById(refreshToken);
    }
}
