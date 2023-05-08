package com.zelusik.eatery.controller;

import com.zelusik.eatery.constant.member.LoginType;
import com.zelusik.eatery.dto.auth.AppleOAuthUserInfo;
import com.zelusik.eatery.dto.auth.KakaoOAuthUserInfo;
import com.zelusik.eatery.dto.auth.request.AppleLoginRequest;
import com.zelusik.eatery.dto.auth.request.KakaoLoginRequest;
import com.zelusik.eatery.dto.auth.request.TokenRefreshRequest;
import com.zelusik.eatery.dto.auth.response.LoginResponse;
import com.zelusik.eatery.dto.auth.response.TokenResponse;
import com.zelusik.eatery.dto.auth.response.TokenValidateResponse;
import com.zelusik.eatery.dto.member.MemberDto;
import com.zelusik.eatery.dto.member.response.LoggedInMemberResponse;
import com.zelusik.eatery.service.AppleOAuthService;
import com.zelusik.eatery.service.JwtTokenService;
import com.zelusik.eatery.service.KakaoOAuthService;
import com.zelusik.eatery.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Tag(name = "로그인 등 인증 관련")
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final KakaoOAuthService kakaoOAuthService;
    private final AppleOAuthService appleOAuthService;
    private final MemberService memberService;
    private final JwtTokenService jwtTokenService;

    @Operation(
            summary = "카카오 로그인",
            description = "<p>Kakao에서 전달받은 access token으로 로그인합니다." +
                    "<p>로그인에 성공하면 로그인 사용자 정보, access token. refresh token을 응답합니다. 이후 로그인 원한이 필요한 API를 호출할 때는 HTTP header의 <strong>Authorization</strong>에 access token을 담아서 요청해야 합니다." +
                    "<p>Access token의 만료기한은 하루, refresh token의 만료기한은 1달입니다." +
                    "<p>이전에 탈퇴했던 회원이고 DB에서 완전히 삭제되지 않은 경우, 재가입을 진행합니다." +
                    "<p>사용자 정보에 포함된 약관 동의 정보(<code>termsInfo</code>)는 아직 약관 동의를 진행하지 않은 경우 <code>null</code>입니다."
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(description = "[10401] 유효하지 않은 kakao access token으로 요청한 경우.", responseCode = "401", content = @Content)
    })
    @PostMapping("/login/kakao")
    public LoginResponse kakaoLogin(@Valid @RequestBody KakaoLoginRequest request) {
        KakaoOAuthUserInfo userInfo = kakaoOAuthService.getUserInfo(request.getKakaoAccessToken());

        MemberDto memberDto = memberService.findOptionalDtoBySocialUidWithDeleted(userInfo.getSocialUid())
                .orElseGet(() -> memberService.save(userInfo.toMemberDto()));

        if (memberDto.getDeletedAt() != null) {
            memberService.rejoin(memberDto.getId());
        }

        TokenResponse tokenResponse = jwtTokenService.create(memberDto.getId(), LoginType.KAKAO);

        return LoginResponse.of(LoggedInMemberResponse.from(memberDto), tokenResponse);
    }

    @Operation(
            summary = "애플 로그인",
            description = "<p>Apple에서 전달받은 identity token으로 로그인합니다." +
                    "<p>로그인에 성공하면 로그인 사용자 정보, access token. refresh token을 응답합니다. 이후 로그인 원한이 필요한 API를 호출할 때는 HTTP header의 <strong>Authorization</strong>에 access token을 담아서 요청해야 합니다." +
                    "<p>Access token의 만료기한은 하루, refresh token의 만료기한은 1달입니다." +
                    "<p>이전에 탈퇴했던 회원이고 DB에서 완전히 삭제되지 않은 경우, 재가입을 진행합니다." +
                    "<p>사용자 정보에 포함된 약관 동의 정보(<code>termsInfo</code>)는 아직 약관 동의를 진행하지 않은 경우 <code>null</code>입니다."
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(description = "[1502] 유효하지 않은 token으로 요청한 경우. Token 값이 잘못되었거나 만료되어 유효하지 않은 경우로 token 갱신 필요", responseCode = "401", content = @Content),
            @ApiResponse(description = "[20000] Apple 로그인 과정에서 알 수 없는 에러가 발생한 경우. 서버 관리자에게 문의해주세요.", responseCode = "500", content = @Content)
    })
    @PostMapping("/login/apple")
    public LoginResponse appleLogin(@Valid @RequestBody AppleLoginRequest request) {
        AppleOAuthUserInfo userInfo = appleOAuthService.getUserInfo(request.getIdentityToken());

        MemberDto memberDto = memberService.findOptionalDtoBySocialUidWithDeleted(userInfo.getSub())
                .orElseGet(() -> memberService.save(userInfo.toMemberDto(request.getName())));

        if (memberDto.getDeletedAt() != null) {
            memberService.rejoin(memberDto.getId());
        }

        TokenResponse tokenResponse = jwtTokenService.create(memberDto.getId(), LoginType.APPLE);

        return LoginResponse.of(LoggedInMemberResponse.from(memberDto), tokenResponse);
    }

    @Operation(
            summary = "토큰 갱신하기",
            description = "<p>기존 발급받은 refresh token으로 새로운 access token과 refresh token을 발급 받습니다."
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200", content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(description = "[1502] 유효하지 않은 token으로 요청한 경우. Token 값이 잘못되었거나 만료되어 유효하지 않은 경우로 token 갱신 필요", responseCode = "401", content = @Content),
    })
    @PostMapping("/token")
    public TokenResponse tokenRefresh(@Valid @RequestBody TokenRefreshRequest request) {
        return jwtTokenService.refresh(request.getRefreshToken());
    }

    @Operation(
            summary = "Refresh token 유효성 검사",
            description = "<p>Refresh token의 유효성을 확인합니다.</p>" +
                    "<p>유효하지 않은 refresh token이란 다음과 같은 경우를 말합니다.</p>" +
                    "<ul>" +
                    "<li>Refresh token의 값이 잘못된 경우</li>" +
                    "<li>Refresh token이 만료된 경우</li>" +
                    "<li>Refresh token의 발행 기록을 찾을 수 없는 경우</li>" +
                    "</ul>"
    )
    @GetMapping("/validity")
    public TokenValidateResponse validate(@RequestParam @NotBlank String refreshToken) {
        return new TokenValidateResponse(jwtTokenService.validateOfRefreshToken(refreshToken));
    }
}
