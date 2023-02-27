package com.zelusik.eatery.app.dto.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.app.domain.constant.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("[DTO] Kakao 사용자 정보 응답 데이터 테스트")
class KakaoOAuthUserInfoTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @DisplayName("Kakao 인증 응답이 주어지면 KakaoOAuthInfo 객체로 변환한다.")
    @Test
    void givenKakaoOAuthResponse_whenInstantiating_thenConvertKakaoOAuthInfoObject() throws Exception {
        // given
        String response = """
                {
                    "id": 1234567890,
                    "connected_at": "2023-01-21T13:07:45Z",
                    "properties": {
                        "nickname": "홍길동",
                        "profile_image": "https://oopy.lazyrockets.com/api/v2/notion/image?src=https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2F1558fc9e-fdfe-4804-b6e7-47a9d0e746de%2Fcmc_bread_white_small_logo.png&blockId=693ceac0-67fd-4bdc-afb9-b172f9aef66b&width=256",
                        "thumbnail_image": "https://oopy.lazyrockets.com/api/v2/notion/image?src=https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2F1558fc9e-fdfe-4804-b6e7-47a9d0e746de%2Fcmc_bread_white_small_logo.png&blockId=693ceac0-67fd-4bdc-afb9-b172f9aef66b&width=256"
                    },
                    "kakao_account": {
                        "profile_nickname_needs_agreement": false,
                        "profile_image_needs_agreement": false,
                        "profile": {
                            "nickname": "홍길동",
                            "thumbnail_image_url": "https://oopy.lazyrockets.com/api/v2/notion/image?src=https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2F1558fc9e-fdfe-4804-b6e7-47a9d0e746de%2Fcmc_bread_white_small_logo.png&blockId=693ceac0-67fd-4bdc-afb9-b172f9aef66b&width=256",
                            "profile_image_url": "https://oopy.lazyrockets.com/api/v2/notion/image?src=https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2F1558fc9e-fdfe-4804-b6e7-47a9d0e746de%2Fcmc_bread_white_small_logo.png&blockId=693ceac0-67fd-4bdc-afb9-b172f9aef66b&width=256",
                            "is_default_image": false
                        },
                        "has_email": true,
                        "email_needs_agreement": false,
                        "is_email_valid": true,
                        "is_email_verified": true,
                        "email": "test@kakao.com",
                        "has_age_range": true,
                        "age_range_needs_agreement": false,
                        "age_range": "20~29",
                        "has_gender": true,
                        "gender_needs_agreement": false,
                        "gender": "male"
                    }
                }
                """;
        Map<String, Object> attributes = mapper.readValue(response, new TypeReference<>() {
        });

        // when
        KakaoOAuthUserInfo result = KakaoOAuthUserInfo.from(attributes);

        // then
        assertThat(result.getSocialUid()).isEqualTo(1234567890L);
        assertThat(result.connectedAt()).isEqualTo(
                ZonedDateTime.of(2023, 1, 21, 13, 7, 45, 0, ZoneOffset.UTC)
                        .withZoneSameInstant(ZoneId.systemDefault())
                        .toLocalDateTime()
        );
        assertThat(result.getNickname()).isEqualTo("홍길동");
        assertThat(result.getThumbnailImageUrl()).isNotNull();
        assertThat(result.getProfileImageUrl()).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@kakao.com");
        assertThat(result.getAgeRange()).isEqualTo(20);
        assertThat(result.getGender()).isEqualTo(Gender.MALE);
    }
}