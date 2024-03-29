package com.zelusik.eatery.domain.member.dto.response;

import com.zelusik.eatery.domain.member.dto.MemberWithProfileInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class GetMemberProfileInfoResponse {

    @Schema(description = "회원 id(PK)", example = "1")
    private Long id;

    @Schema(description = "로그인한 사용자와 조회된 회원이 동일한 회원인지 여부", example = "false")
    private Boolean isEqualLoginMember;

    @Schema(description = "프로필 이미지")
    private MemberProfileImageResponse profileImage;

    @Schema(description = "닉네임", example = "우기")
    private String nickname;

    @Schema(description = "작성한 리뷰 수", example = "62")
    private Integer numOfReviews;

    @Schema(description = "영향력", example = "250")
    private Integer influence;

    @Schema(description = "팔로워 수", example = "763")
    private Integer numOfFollowers;

    @Schema(description = "팔로잉 수", example = "68")
    private Integer numOfFollowings;

    @Schema(description = "취향 통계 정보")
    private MemberTasteStatisticsResponse tasteStatistics;

    public static GetMemberProfileInfoResponse from(long loginMemberId, MemberWithProfileInfoDto memberWithProfileInfoDto) {
        return new GetMemberProfileInfoResponse(
                memberWithProfileInfoDto.getId(),
                loginMemberId == memberWithProfileInfoDto.getId(),
                new MemberProfileImageResponse(
                        memberWithProfileInfoDto.getProfileImageUrl(),
                        memberWithProfileInfoDto.getProfileThumbnailImageUrl()
                ),
                memberWithProfileInfoDto.getNickname(),
                memberWithProfileInfoDto.getNumOfReviews(),
                memberWithProfileInfoDto.getInfluence(),
                memberWithProfileInfoDto.getNumOfFollowers(),
                memberWithProfileInfoDto.getNumOfFollowings(),
                new MemberTasteStatisticsResponse(
                        memberWithProfileInfoDto.getMostVisitedLocation(),
                        memberWithProfileInfoDto.getMostTaggedReviewKeyword() != null ? memberWithProfileInfoDto.getMostTaggedReviewKeyword().getContent() : "",
                        memberWithProfileInfoDto.getMostEatenFoodCategory() != null ? memberWithProfileInfoDto.getMostEatenFoodCategory().getCategoryName() : ""
                )
        );
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class MemberProfileImageResponse {

        @Schema(description = "이미지 url", example = "https://member-profile-image-url")
        private String imageUrl;

        @Schema(description = "썸네일 이미지 url", example = "https://member-profile-thumbnail-image-url")
        private String thumbnailImageUrl;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class MemberTasteStatisticsResponse {

        @Schema(description = "가장 많이 방문한 장소(읍면동)", example = "연남동")
        private String mostVisitedLocation;

        @Schema(description = "가장 많이 태그된 리뷰 키워드", example = "데이트에 최고")
        private String mostTaggedReviewKeyword;

        @Schema(description = "가장 많이 먹은 음식 카테고리", example = "한식")
        private String mostEatenFoodCategory;
    }
}
