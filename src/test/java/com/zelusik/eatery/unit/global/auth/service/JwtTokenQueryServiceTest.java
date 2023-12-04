package com.zelusik.eatery.unit.global.auth.service;

import com.zelusik.eatery.global.auth.exception.TokenValidateException;
import com.zelusik.eatery.global.auth.repository.RefreshTokenRepository;
import com.zelusik.eatery.global.auth.service.JwtTokenQueryService;
import com.zelusik.eatery.global.auth.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@DisplayName("[Unit] Service(Query) - Jwt token")
@ExtendWith(MockitoExtension.class)
class JwtTokenQueryServiceTest {

    @InjectMocks
    private JwtTokenQueryService sut;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

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
}