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
    private String token;
    private Long memberId;

    public static RefreshToken of(String token, Long memberId) {
        return new RefreshToken(token, memberId);
    }
}
