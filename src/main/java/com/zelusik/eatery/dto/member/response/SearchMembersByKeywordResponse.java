package com.zelusik.eatery.dto.member.response;

import com.zelusik.eatery.dto.member.MemberDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SearchMembersByKeywordResponse {

    @Schema(description = "회원 id(PK)", example = "1")
    private Long id;

    @Schema(description = "프로필 이미지")
    private String profileThumbnailImage;

    @Schema(description = "닉네임", example = "우기")
    private String nickname;

    public static SearchMembersByKeywordResponse from(MemberDto dto) {
        return new SearchMembersByKeywordResponse(
                dto.getId(),
                dto.getProfileThumbnailImageUrl(),
                dto.getNickname()
        );
    }
}
