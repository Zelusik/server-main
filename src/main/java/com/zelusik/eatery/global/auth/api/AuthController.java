package com.zelusik.eatery.global.auth.api;

import com.zelusik.eatery.global.auth.dto.response.RefreshTokenResponse;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.global.apple.dto.AppleOAuthUserInfo;
import com.zelusik.eatery.global.apple.service.AppleOAuthService;
import com.zelusik.eatery.global.auth.dto.JwtTokenDto;
import com.zelusik.eatery.global.auth.dto.request.AppleLoginRequest;
import com.zelusik.eatery.global.auth.dto.request.KakaoLoginRequest;
import com.zelusik.eatery.global.auth.dto.request.RefreshTokensRequest;
import com.zelusik.eatery.global.auth.dto.response.LoginResponse;
import com.zelusik.eatery.global.auth.dto.response.TokenValidateResponse;
import com.zelusik.eatery.global.auth.service.JwtTokenService;
import com.zelusik.eatery.global.kakao.dto.KakaoOAuthUserInfo;
import com.zelusik.eatery.global.kakao.service.KakaoService;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.member.service.MemberService;
import com.zelusik.eatery.domain.terms_info.dto.TermsInfoDto;
import com.zelusik.eatery.domain.terms_info.service.TermsInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Set;

import static com.zelusik.eatery.global.common.constant.ConstantUtil.API_MINOR_VERSION_HEADER_NAME;

@Tag(name = "로그인 등 인증 관련 API")
@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
@RestController
public class AuthController {

    private final KakaoService kakaoService;
    private final AppleOAuthService appleOAuthService;
    private final MemberService memberService;
    private final JwtTokenService jwtTokenService;
    private final TermsInfoService termsInfoService;

    @Operation(
            summary = "카카오 로그인",
            description = """
                    <p><strong>Latest version: v1.1</strong>
                    <p>Kakao에서 전달받은 access token으로 로그인합니다.
                    <p>로그인에 성공하면 로그인 사용자 정보, access token. refresh token을 응답합니다. 이후 로그인 원한이 필요한 API를 호출할 때는 HTTP header의 <strong>Authorization</strong>에 access token을 담아서 요청해야 합니다.
                    <p>Access token의 만료기한은 하루, refresh token의 만료기한은 1달입니다.
                    <p>이전에 탈퇴했던 회원이고 DB에서 완전히 삭제되지 않은 경우, 재가입을 진행합니다.
                    <p>사용자 정보에 포함된 약관 동의 정보(<code>termsInfo</code>)는 아직 약관 동의를 진행하지 않은 경우 <code>null</code>입니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "[10401] 유효하지 않은 kakao access token으로 요청한 경우.", responseCode = "401", content = @Content)
    })
    @PostMapping(value = "/v1/auth/login/kakao", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public LoginResponse kakaoLoginV1_1(@Valid @RequestBody KakaoLoginRequest request) {
        KakaoOAuthUserInfo userInfo = kakaoService.getUserInfo(request.getKakaoAccessToken());

        MemberDto memberDto = memberService.findOptionalDtoBySocialUidWithDeleted(userInfo.getSocialUid())
                .orElseGet(() -> memberService.save(userInfo.toMemberDto(Set.of(RoleType.USER))));
        if (memberDto.getDeletedAt() != null) {
            memberService.rejoin(memberDto.getId());
        }

        TermsInfoDto termsInfoDto = termsInfoService.findOptionalDtoByMemberId(memberDto.getId()).orElse(null);

        JwtTokenDto jwtTokenDto = jwtTokenService.create(memberDto.getId(), LoginType.KAKAO);

        return LoginResponse.from(memberDto, termsInfoDto, jwtTokenDto);
    }

    @Operation(
            summary = "애플 로그인",
            description = """
                    <p><strong>Latest version: v1.1</strong>
                    <p>Apple에서 전달받은 identity token으로 로그인합니다.
                    <p>로그인에 성공하면 로그인 사용자 정보, access token. refresh token을 응답합니다. 이후 로그인 원한이 필요한 API를 호출할 때는 HTTP header의 <strong>Authorization</strong>에 access token을 담아서 요청해야 합니다.
                    <p>Access token의 만료기한은 하루, refresh token의 만료기한은 1달입니다.
                    <p>이전에 탈퇴했던 회원이고 DB에서 완전히 삭제되지 않은 경우, 재가입을 진행합니다.
                    <p>사용자 정보에 포함된 약관 동의 정보(<code>termsInfo</code>)는 아직 약관 동의를 진행하지 않은 경우 <code>null</code>입니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "[1502] 유효하지 않은 token으로 요청한 경우. Token 값이 잘못되었거나 만료되어 유효하지 않은 경우로 token 갱신 필요", responseCode = "401", content = @Content),
            @ApiResponse(description = "[20000] Apple 로그인 과정에서 알 수 없는 에러가 발생한 경우. 서버 관리자에게 문의해주세요.", responseCode = "500", content = @Content)
    })
    @PostMapping(value = "/v1/auth/login/apple", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public LoginResponse appleLoginV1_1(@Valid @RequestBody AppleLoginRequest request) {
        AppleOAuthUserInfo userInfo = appleOAuthService.getUserInfo(request.getIdentityToken());

        MemberDto memberDto = memberService.findOptionalDtoBySocialUidWithDeleted(userInfo.getSub())
                .orElseGet(() -> memberService.save(userInfo.toMemberDto(request.getName(), Set.of(RoleType.USER))));
        if (memberDto.getDeletedAt() != null) {
            memberService.rejoin(memberDto.getId());
        }

        TermsInfoDto termsInfoDto = termsInfoService.findOptionalDtoByMemberId(memberDto.getId()).orElse(null);

        JwtTokenDto jwtTokenDto = jwtTokenService.create(memberDto.getId(), LoginType.APPLE);

        return LoginResponse.from(memberDto, termsInfoDto, jwtTokenDto);
    }

    @Operation(
            summary = "토큰 갱신하기",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>기존 발급받은 refresh token으로 새로운 access token과 refresh token을 발급 받습니다."
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "[1504] 유효하지 않은 refresh token으로 요청한 경우. Token 값이 잘못되었거나 만료되어 유효하지 않은 경우로 token 갱신 필요", responseCode = "401", content = @Content),
    })
    @PostMapping(value = "/v1/auth/token", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public RefreshTokenResponse refreshTokensV1_1(@Valid @RequestBody RefreshTokensRequest request) {
        JwtTokenDto jwtTokenDto = jwtTokenService.refresh(request.getRefreshToken());
        return RefreshTokenResponse.from(jwtTokenDto);
    }

    @Operation(
            summary = "Refresh token 유효성 검사",
            description = """
                    <p><strong>Latest version: v1.1</strong>
                    <p>Refresh token의 유효성을 확인합니다.</p>
                    <p>유효하지 않은 refresh token이란 다음과 같은 경우를 말합니다.</p>
                    <ul>
                        <li>Refresh token의 값이 잘못된 경우</li>
                        <li>Refresh token이 만료된 경우</li>
                        <li>Refresh token의 발행 기록을 찾을 수 없는 경우</li>
                    </ul>
                    """
    )
    @GetMapping(value = "/v1/auth/validity", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public TokenValidateResponse validateRefreshTokenV1_1(@RequestParam @NotBlank String refreshToken) {
        return new TokenValidateResponse(jwtTokenService.validateOfRefreshToken(refreshToken));
    }
}
