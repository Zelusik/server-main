package com.zelusik.eatery.app.dto.auth;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")  // TODO: Map -> Object 변환 로직이 있어서 generic type casting 문제를 무시한다. 더 좋은 방법이 있다면 고려할 수 있음.
public record AppleOAuthPublicKeyResponse(
        List<Key> keys
) {
    public record Key(
            String kty,
            String kid,
            String use,
            String alg,
            String n,
            String e
    ) {
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

    public static AppleOAuthPublicKeyResponse from(Map<String, Object> attributes) {
        List<Map<String, Object>> keyMap = (List<Map<String, Object>>) attributes.get("keys");
        List<Key> keys = keyMap.stream()
                .map(Key::from)
                .toList();
        return new AppleOAuthPublicKeyResponse(keys);
    }

    public Key getMatchedKeyBy(String kid, String alg) {
        return this.keys().stream()
                .filter(key -> key.kid().equals(kid) && key.alg().equals(alg))
                .findFirst()
                .orElseThrow(() -> new NullPointerException());
    }
}
