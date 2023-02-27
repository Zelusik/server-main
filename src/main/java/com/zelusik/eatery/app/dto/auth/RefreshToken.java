package com.zelusik.eatery.app.dto.auth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@RedisHash(value = "refreshToken", timeToLive = 30 * 24 * 60 * 60)
public class RefreshToken {

    @Id
    private Long memberId;
    private String token;

    public static RefreshToken of(Long memberId, String token) {
        return new RefreshToken(memberId, token);
    }
}
