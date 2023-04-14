package com.zelusik.eatery.dto.auth.response;

import com.zelusik.eatery.dto.member.response.LoggedInMemberResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LoginResponse {

    @Schema(description = "로그인 유저 정보")
    LoggedInMemberResponse loggedInMember;

    @Schema(description = "Token 정보")
    TokenResponse tokens;

    public static LoginResponse of(LoggedInMemberResponse loggedInMember, TokenResponse tokens) {
        return new LoginResponse(loggedInMember, tokens);
    }
}
