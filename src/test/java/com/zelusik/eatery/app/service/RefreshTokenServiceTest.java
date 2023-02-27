package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.dto.auth.RefreshToken;
import com.zelusik.eatery.app.repository.RefreshTokenRepository;
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
class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService sut;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @DisplayName("회원 정보와 refresh token이 주어지면 redis에 refresh token을 저장한다.")
    @Test
    void givenMemberIdAndRefreshToken_whenSaving_thenSaveRefreshToken() {
        // given
        Long memberId = 1L;
        String refreshToken = "test";
        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(any(RefreshToken.class));

        // when
        sut.save(memberId, refreshToken);

        // then
        then(refreshTokenRepository).should().save(any(RefreshToken.class));
    }
}