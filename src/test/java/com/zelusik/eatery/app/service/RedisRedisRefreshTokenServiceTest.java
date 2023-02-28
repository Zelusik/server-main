package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.dto.auth.RedisRefreshToken;
import com.zelusik.eatery.app.repository.RedisRefreshTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Service] RefreshToken")
@ExtendWith(MockitoExtension.class)
class RedisRedisRefreshTokenServiceTest {

    @InjectMocks
    private RedisRefreshTokenService sut;

    @Mock
    private RedisRefreshTokenRepository redisRefreshTokenRepository;

    @DisplayName("회원 정보와 refresh token이 주어지면 redis에 refresh token을 저장한다.")
    @Test
    void givenMemberIdAndRefreshToken_whenSaving_thenSaveRefreshToken() {
        // given
        Long memberId = 1L;
        String refreshToken = "test";
        given(redisRefreshTokenRepository.save(any(RedisRefreshToken.class))).willReturn(any(RedisRefreshToken.class));

        // when
        sut.save(refreshToken, memberId);

        // then
        then(redisRefreshTokenRepository).should().save(any(RedisRefreshToken.class));
    }
}