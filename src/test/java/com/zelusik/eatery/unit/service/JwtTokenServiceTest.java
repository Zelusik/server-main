package com.zelusik.eatery.unit.service;

import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.global.auth.dto.JwtTokenDto;
import com.zelusik.eatery.global.auth.entity.RefreshToken;
import com.zelusik.eatery.global.auth.exception.TokenValidateException;
import com.zelusik.eatery.global.auth.repository.RefreshTokenRepository;
import com.zelusik.eatery.global.security.dto.JwtTokenInfoDto;
import com.zelusik.eatery.global.security.JwtTokenProvider;
import com.zelusik.eatery.global.auth.service.JwtTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("[Unit] JwtToken Service")
@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

    @InjectMocks
    private JwtTokenService sut;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("회원 PK와 로그인 타입이 주어지면 access, refresh token을 생성하고 " +
                 "refresh token을 redis에 저장한 후, 생성된 token들을 반환한다.")
    @Test
    void givenMemberIdAndLoginType_whenCreateJwtTokens_thenSaveAndReturnTokens() {
        // given
        Long memberId = 1L;
        LoginType loginType = LoginType.KAKAO;
        JwtTokenInfoDto expectedJwtToken = createJwtToken();
        given(jwtTokenProvider.createAccessToken(memberId, loginType))
                .willReturn(expectedJwtToken);
        given(jwtTokenProvider.createRefreshToken(memberId, loginType))
                .willReturn(expectedJwtToken);
        given(refreshTokenRepository.save(any(RefreshToken.class)))
                .willReturn(createRedisRefreshToken(memberId));

        // when
        JwtTokenDto jwtTokenDto = sut.create(memberId, loginType);
        String actualAccessToken = jwtTokenDto.accessToken();
        String actualRefreshToken = jwtTokenDto.refreshToken();

        // then
        then(jwtTokenProvider).should().createRefreshToken(memberId, loginType);
        then(refreshTokenRepository).should().save(any(RefreshToken.class));
        assertThat(actualAccessToken).isEqualTo(expectedJwtToken.token());
        assertThat(actualRefreshToken).isEqualTo(expectedJwtToken.token());
    }

    @DisplayName("Refresh token이 주어지면 주어진 token을 삭제한 후, " +
                 "새로운 access token과 refresh token을 생성하고 반환한다.")
    @Test
    void givenOldRefreshToken_whenRefresh_thenDeleteOldTokenAndSaveAndReturnNewTokens() {
        // given
        Long memberId = 1L;
        LoginType loginType = LoginType.KAKAO;

        String oldRefreshToken = createJwtToken("old").token();
        RefreshToken oldRedisRefreshToken = createRedisRefreshToken(memberId, oldRefreshToken);

        JwtTokenInfoDto expectedNewJwtToken = createJwtToken("new");

        given(jwtTokenProvider.createAccessToken(memberId, loginType))
                .willReturn(expectedNewJwtToken);
        given(jwtTokenProvider.createRefreshToken(memberId, loginType))
                .willReturn(expectedNewJwtToken);
        willDoNothing().given(jwtTokenProvider).validateToken(oldRefreshToken);
        given(jwtTokenProvider.getLoginType(oldRefreshToken)).willReturn(loginType);

        given(refreshTokenRepository.findById(oldRefreshToken))
                .willReturn(Optional.of(oldRedisRefreshToken));
        willDoNothing().given(refreshTokenRepository).delete(oldRedisRefreshToken);
        given(refreshTokenRepository.save(any(RefreshToken.class)))
                .willReturn(createRedisRefreshToken(memberId));

        // when
        JwtTokenDto jwtTokenDto = sut.refresh(oldRefreshToken);
        String actualNewAccessToken = jwtTokenDto.accessToken();
        String actualNewRefreshToken = jwtTokenDto.refreshToken();

        // then
        then(jwtTokenProvider).should().createRefreshToken(memberId, loginType);
        then(jwtTokenProvider).should().validateToken(oldRefreshToken);
        then(jwtTokenProvider).should().getLoginType(oldRefreshToken);
        then(refreshTokenRepository).should().findById(oldRefreshToken);
        then(refreshTokenRepository).should().delete(oldRedisRefreshToken);
        then(refreshTokenRepository).should().save(any(RefreshToken.class));
        assertThat(actualNewAccessToken).isEqualTo(expectedNewJwtToken.token());
        assertThat(actualNewRefreshToken).isEqualTo(expectedNewJwtToken.token());
    }

    @DisplayName("유효하지 않은 refresh token이 주어지고, token을 갱신하면, 예외가 발생한다.")
    @Test
    void givenNotValidRefreshToken_whenRefresh_thenThrowException() {
        // given
        String refreshToken = "test";
        willThrow(TokenValidateException.class).given(jwtTokenProvider).validateToken(refreshToken);

        // when
        Throwable t = catchThrowable(() -> sut.refresh(refreshToken));

        // then
        then(jwtTokenProvider).should().validateToken(refreshToken);
        then(refreshTokenRepository).shouldHaveNoInteractions();
        then(jwtTokenProvider).shouldHaveNoMoreInteractions();
        assertThat(t).isInstanceOf(TokenValidateException.class);
    }

    @DisplayName("유효한 refresh token이 주어지고, 유효성을 검사하면, true를 반환한다.")
    @Test
    void givenValidRefreshToken_whenValidate_thenReturnTrue() {
        // given
        String refreshToken = "test";
        willDoNothing().given(jwtTokenProvider).validateToken(refreshToken);
        given(refreshTokenRepository.existsById(refreshToken)).willReturn(true);

        // when
        boolean result = sut.validateOfRefreshToken(refreshToken);

        // then
        then(jwtTokenProvider).should().validateToken(refreshToken);
        then(refreshTokenRepository).should().existsById(refreshToken);
        assertThat(result).isTrue();
    }

    @DisplayName("유효하지 않은 refresh token이 주어지고, 유효성을 검사하면, false를 반환한다.")
    @Test
    void givenNotValidRefreshToken_whenValidate_thenReturnFalse() {
        // given
        String refreshToken = "test";
        willThrow(TokenValidateException.class).given(jwtTokenProvider).validateToken(refreshToken);

        // when
        boolean result = sut.validateOfRefreshToken(refreshToken);

        // then
        then(jwtTokenProvider).should().validateToken(refreshToken);
        then(refreshTokenRepository).shouldHaveNoInteractions();
        assertThat(result).isFalse();
    }

    @DisplayName("유효한 refresh token이 주어졌지만 redis에 존재하지 않을 때, 유효성을 검사하면, false를 반환한다.")
    @Test
    void givenValidRefreshTokenButNotExistInRedis_whenValidate_thenReturnFalse() {
        String refreshToken = "test";
        willDoNothing().given(jwtTokenProvider).validateToken(refreshToken);
        given(refreshTokenRepository.existsById(refreshToken)).willReturn(false);

        // when
        boolean result = sut.validateOfRefreshToken(refreshToken);

        // then
        then(jwtTokenProvider).should().validateToken(refreshToken);
        then(refreshTokenRepository).should().existsById(refreshToken);
        assertThat(result).isFalse();
    }

    private JwtTokenInfoDto createJwtToken() {
        return createJwtToken("test");
    }

    private JwtTokenInfoDto createJwtToken(String token) {
        return JwtTokenInfoDto.of(token, LocalDateTime.now());
    }

    private RefreshToken createRedisRefreshToken(Long memberId) {
        return RefreshToken.of("test", memberId);
    }

    private RefreshToken createRedisRefreshToken(Long memberId, String refreshToken) {
        return RefreshToken.of(refreshToken, memberId);
    }
}