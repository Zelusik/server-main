package com.zelusik.eatery.dto.auth;

import com.zelusik.eatery.exception.auth.TokenValidateException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")  // TODO: Map -> Object 변환 로직이 있어서 generic type casting 문제를 무시한다. 더 좋은 방법이 있다면 고려할 수 있음.
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AppleOAuthPublicKey {

    private List<Key> keys;

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class Key {

        private String kty;
        private String kid;
        private String use;
        private String alg;
        private String n;
        private String e;

        public static Key from(Map<String, Object> attributes) {
            return new Key(
                    String.valueOf(attributes.get("kty")),
                    String.valueOf(attributes.get("kid")),
                    String.valueOf(attributes.get("use")),
                    String.valueOf(attributes.get("alg")),
                    String.valueOf(attributes.get("n")),
                    String.valueOf(attributes.get("e"))
            );
        }
    }

    public static AppleOAuthPublicKey from(Map<String, Object> attributes) {
        List<Map<String, Object>> keyMap = (List<Map<String, Object>>) attributes.get("keys");
        List<Key> keys = keyMap.stream()
                .map(Key::from)
                .toList();
        return new AppleOAuthPublicKey(keys);
    }

    public Key getMatchedKeyBy(String kid, String alg) {
        return this.getKeys().stream()
                .filter(key -> key.getKid().equals(kid) && key.getAlg().equals(alg))
                .findFirst()
                .orElseThrow(() ->
                        new TokenValidateException("Apple 공개키 검증 과정에서 발생한 에러."));
    }
}
