package com.zelusik.eatery.app.controller;

import com.zelusik.eatery.app.domain.constant.LoginType;
import com.zelusik.eatery.app.dto.auth.KakaoOAuthUserInfo;
import com.zelusik.eatery.app.dto.auth.request.KakaoLoginRequest;
import com.zelusik.eatery.app.dto.auth.request.TokenRefreshRequest;
import com.zelusik.eatery.app.dto.auth.response.LoginResponse;
import com.zelusik.eatery.app.dto.auth.response.TokenResponse;
import com.zelusik.eatery.app.dto.member.MemberDto;
import com.zelusik.eatery.app.dto.member.response.LoggedInMemberResponse;
import com.zelusik.eatery.app.service.JwtTokenService;
import com.zelusik.eatery.app.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "로그인 등 인증 관련")
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final KakaoOAuthService kakaoOAuthService;
    private final MemberService memberService;
    private final JwtTokenService jwtTokenService;

    @Operation(
            summary = "로그인",
            description = "<p>Kakao에서 전달받은 access token을 request header에 담아 로그인합니다.</p>" +
                    "<p>로그인에 성공하면 로그인 사용자 정보, access token. refresh token을 응답합니다.</p>" +
                    "<p>사용자 정보에 포함된 약관 동의 정보(<code>termsInfo</code>)는 아직 약관 동의를 진행하지 않은 경우 <code>null</code>입니다.</p>"
    )
    @PostMapping("/login/kakao")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody KakaoLoginRequest request) {
        KakaoOAuthUserInfo userInfo = kakaoOAuthService.getUserInfo(request.getKakaoAccessToken());

        MemberDto memberDto = memberService.findOptionalDtoBySocialUid(userInfo.getSocialUid())
                .orElseGet(() -> memberService.save(userInfo.toMemberDto()));

        TokenResponse tokenResponse = jwtTokenService.createJwtTokens(memberDto.id(), LoginType.KAKAO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(LoginResponse.of(LoggedInMemberResponse.from(memberDto), tokenResponse));
    }

    @Operation(
            summary = "토큰 갱신하기",
            description = "<p>기존 발급받은 refresh token으로 새로운 access token과 refresh token을 발급 받습니다.</p>"
    )
    @PostMapping("/token")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        TokenResponse tokenResponse = jwtTokenService.refresh(request.getRefreshToken());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokenResponse);
    }
}
