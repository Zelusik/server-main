package com.zelusik.eatery.global.security;

import com.zelusik.eatery.app.domain.constant.LoginType;
import com.zelusik.eatery.app.domain.constant.RoleType;
import com.zelusik.eatery.global.exception.auth.TokenValidateException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;

    // Expiration Time
    private static final Long MINUTE = 1000 * 60L;
    private static final Long HOUR = 60 * MINUTE;
    private static final Long DAY = 24 * HOUR;
    private static final long ACCESS_TOKEN_EXPIRED_DURATION = 12 * HOUR; // Access token 만료시간 : 12시간
    public static final long REFRESH_TOKEN_EXPIRED_DURATION = 30 * DAY; // Refresh token 만료시간 : 한 달

    private static final String TOKEN_TYPE_BEARER = "Bearer ";

    private static final String ROLE_CLAIM_KEY = "role";
    private static final String LOGIN_TYPE_CLAIM_KEY = "loginType";

    @Value("${jwt.secret.key:default_secret_key_for_local_env_ge_256bit}")
    private String salt;
    private Key secretKey;

    /**
     * 객체 초기화, jwt secret key를 Base64로 인코딩
     */
    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Jwt access token을 생성하여 반환한다.
     *
     * @param memberId 로그인하려는 회원의 id(PK)
     * @return 생성한 jwt token
     */
    public String createAccessToken(Long memberId, LoginType loginType) {
        return createJwtToken(memberId, RoleType.USER, loginType, ACCESS_TOKEN_EXPIRED_DURATION);
    }

    /**
     * Jwt refresh token을 생성하여 반환한다.
     *
     * @param memberId 로그인하려는 회원의 id(PK)
     * @return 생성한 jwt token
     */
    public String createRefreshToken(Long memberId, LoginType loginType) {
        return createJwtToken(memberId, RoleType.USER, loginType, REFRESH_TOKEN_EXPIRED_DURATION);
    }

    /**
     * JWT token에서 사용자 정보 조회 후 security login 과정(UsernamePasswordAuthenticationToken)을 수행한다.
     *
     * @param token Jwt token
     * @return Token을 통해 조회한 사용자 정보
     */
    public Authentication getAuthentication(String token) {
        UserDetails principal = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    /**
     * Request의 header에서 token을 읽어온다.
     *
     * @param request Request 객체
     * @return Header에서 추출한 token
     */
    public String getToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_TYPE_BEARER)) {
            return null;
        }
        return authorizationHeader.substring(TOKEN_TYPE_BEARER.length());
    }

    /**
     * 토큰의 유효성, 만료일자 검증
     *
     * @param token 검증하고자 하는 JWT token
     * @throws io.jsonwebtoken.UnsupportedJwtException     if the claimsJws argument does not represent an Claims JWS
     * @throws io.jsonwebtoken.MalformedJwtException       if the claimsJws string is not a valid JWS
     * @throws io.jsonwebtoken.security.SignatureException if the claimsJws JWS signature validation fails
     * @throws io.jsonwebtoken.ExpiredJwtException         if the specified JWT is a Claims JWT and the Claims has an expiration time before the time this method is invoked.
     * @throws IllegalArgumentException                    if the claimsJws string is null or empty or only whitespace
     */
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            throw new RuntimeException();
        } catch (
                UnsupportedJwtException |
                MalformedJwtException |
                SignatureException |
                ExpiredJwtException ex
        ) {
            throw ex;
        } catch (Exception ex) {
            throw new TokenValidateException(ex);
        }
    }

    /**
     * Subject(socialUid), 로그인 type, token 만료 시간을 전달받아 JWT token을 생성한다.
     * 현재 access token과 refresh token을 생성할 때 만료 시간 외의 정보는 동일하므로 method를 통일하였다.
     *
     * @param memberId             회원의 id(PK)
     * @param loginType            회원의 로그인 type
     * @param tokenExpiredDuration Token 만료 시간
     * @return 생성된 JWT token
     */
    private String createJwtToken(Long memberId, RoleType roleType, LoginType loginType, Long tokenExpiredDuration) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(String.valueOf(memberId))
                .claim(ROLE_CLAIM_KEY, roleType.getName())
                .claim(LOGIN_TYPE_CLAIM_KEY, loginType.name())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenExpiredDuration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 회원 정보(username)를 추출한다. 이 때 username은 회원의 id(PK) 값.
     *
     * @param token Jwt token
     * @return 추출한 회원 정보(username == email)
     */
    private String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
