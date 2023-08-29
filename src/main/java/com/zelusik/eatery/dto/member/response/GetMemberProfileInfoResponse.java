package com.zelusik.eatery.dto.member.response;

import com.zelusik.eatery.dto.member.MemberProfileInfoDto;
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

    public static GetMemberProfileInfoResponse from(long loginMemberId, MemberProfileInfoDto memberProfileInfoDto) {
        return new GetMemberProfileInfoResponse(
                memberProfileInfoDto.getId(),
                loginMemberId == memberProfileInfoDto.getId(),
                new MemberProfileImageResponse(
                        memberProfileInfoDto.getProfileImageUrl(),
                        memberProfileInfoDto.getProfileThumbnailImageUrl()
                ),
                memberProfileInfoDto.getNickname(),
                memberProfileInfoDto.getNumOfReviews(),
                memberProfileInfoDto.getInfluence(),
                memberProfileInfoDto.getNumOfFollowers(),
                memberProfileInfoDto.getNumOfFollowings(),
                new MemberTasteStatisticsResponse(
                        memberProfileInfoDto.getMostVisitedLocation(),
                        memberProfileInfoDto.getMostTaggedReviewKeyword() != null ? memberProfileInfoDto.getMostTaggedReviewKeyword().getDescription() : "",
                        memberProfileInfoDto.getMostEatenFoodCategory() != null ? memberProfileInfoDto.getMostEatenFoodCategory().getName() : ""
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
