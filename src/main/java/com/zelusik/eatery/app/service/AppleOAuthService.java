package com.zelusik.eatery.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.app.dto.auth.AppleOAuthPublicKey;
import com.zelusik.eatery.app.dto.auth.AppleOAuthUserInfo;
import com.zelusik.eatery.global.exception.auth.AppleOAuthLoginException;
import com.zelusik.eatery.global.exception.auth.TokenValidateException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

@Service
public class AppleOAuthService {

    private final ObjectMapper objectMapper;
    private final HttpRequestService httpRequestService;

    public AppleOAuthService(HttpRequestService httpRequestService) {
        this.objectMapper = new ObjectMapper();
        this.httpRequestService = httpRequestService;
    }

    /**
     * <p>Identity token에서 회원 정보를 읽어온다.
     * <p>읽어오는 정보는 다음 네 가지이다.
     * <ul>
     *     <li>sub(유저 고유 값)</li>
     *     <li>이메일</li>
     *     <li>이메일 유효 여부</li>
     *     <li>private 이메일 여부</li>
     * </ul>
     *
     * @param identityToken 회원 정보가 담긴 identity token
     * @return 회원 정보
     */
    public AppleOAuthUserInfo getUserInfo(String identityToken) {
        Map<String, Object> headerOfIdentityToken;
        try {
            headerOfIdentityToken = new ObjectMapper().readValue(
                    Base64.getDecoder().decode(identityToken.substring(0, identityToken.indexOf("."))),
                    new TypeReference<>() {
                    }
            );
        } catch (IOException ex) {
            throw new AppleOAuthLoginException(ex);
        }

        PublicKey publicKey = getAppleOAuthPublicKey(headerOfIdentityToken);

        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(identityToken)
                    .getBody();
        } catch (Exception ex) {
            throw new TokenValidateException(ex);
        }

        return AppleOAuthUserInfo.from(claims);
    }

    /**
     * Identity token에서 유저 정보를 decode하기 위해 필요한 public key
     *
     * @param headerOfIdentityToken Identity token의 header (jwt header)
     * @return 유저 정보를 decode하기 위해 새롭게 생성한 public key
     */
    private PublicKey getAppleOAuthPublicKey(Map<String, Object> headerOfIdentityToken) {
        String requestUrl = "https://appleid.apple.com/auth/keys";

        ResponseEntity<String> response;
        try {
            response = httpRequestService.sendHttpRequest(requestUrl, HttpMethod.GET, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> attributes;
        try {
            attributes = objectMapper.readValue(response.getBody(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            attributes = Collections.emptyMap();
        }

        AppleOAuthPublicKey appleOAuthPublicKey = AppleOAuthPublicKey.from(attributes);
        AppleOAuthPublicKey.Key matchedKey = appleOAuthPublicKey.getMatchedKeyBy(
                String.valueOf(headerOfIdentityToken.get("kid")),
                String.valueOf(headerOfIdentityToken.get("alg"))
        );

        BigInteger n = new BigInteger(1, Base64.getUrlDecoder().decode(matchedKey.getN()));
        BigInteger e = new BigInteger(1, Base64.getUrlDecoder().decode(matchedKey.getE()));

        try {
            return KeyFactory.getInstance(matchedKey.getKty())
                    .generatePublic(new RSAPublicKeySpec(n, e));
        } catch (NoSuchAlgorithmException |
                 InvalidKeySpecException ex) {
            throw new AppleOAuthLoginException(ex);
        }
    }
}
