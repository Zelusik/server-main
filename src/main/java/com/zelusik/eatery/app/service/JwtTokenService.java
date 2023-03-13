package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.constant.LoginType;
import com.zelusik.eatery.app.dto.auth.RedisRefreshToken;
import com.zelusik.eatery.app.dto.auth.response.TokenResponse;
import com.zelusik.eatery.app.repository.RedisRefreshTokenRepository;
import com.zelusik.eatery.global.exception.auth.RedisRefreshTokenNotFoundException;
import com.zelusik.eatery.global.exception.auth.TokenValidateException;
import com.zelusik.eatery.global.security.JwtTokenInfoDto;
import com.zelusik.eatery.global.security.JwtTokenProvider;
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
    public TokenResponse createJwtTokens(Long memberId, LoginType loginType) {
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
     * @throws com.zelusik.eatery.global.exception.auth.TokenValidateException 유효하지 않은 token인 경우
     */
    @Transactional
    public TokenResponse refresh(String oldRefreshToken) {
        jwtTokenProvider.validateToken(oldRefreshToken);

        RedisRefreshToken oldRedisRefreshToken = redisRefreshTokenRepository.findById(oldRefreshToken)
                .orElseThrow(TokenValidateException::new);
        redisRefreshTokenRepository.delete(oldRedisRefreshToken);

        return createJwtTokens(
                oldRedisRefreshToken.getMemberId(),
                jwtTokenProvider.getLoginType(oldRefreshToken)
        );
    }
}
