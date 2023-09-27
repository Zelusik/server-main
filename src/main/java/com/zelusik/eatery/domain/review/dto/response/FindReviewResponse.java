package com.zelusik.eatery.domain.review.dto.response;

import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.review_image_menu_tag.entity.MenuTagPoint;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.review.dto.ReviewDto;
import com.zelusik.eatery.domain.review_image.dto.ReviewImageDto;
import com.zelusik.eatery.domain.review_image_menu_tag.dto.ReviewImageMenuTagDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FindReviewResponse {

    @Schema(description = "PK of review", example = "2")
    private Long id;

    @Schema(description = "작성자 정보")
    private WriterResponse writer;

    @Schema(description = "장소 정보")
    private PlaceResponse place;

    @Schema(description = "리뷰 키워드 목록", example = "[\"신선한 재료\", \"왁자지껄한\"]")
    private List<String> keywords;

    @Schema(description = "내용", example = "review content")
    private String content;

    @Schema(description = "리뷰 이미지")
    private List<ReviewImageResponse> reviewImages;

    public static FindReviewResponse from(ReviewDto dto, long loginMemberId) {
        return new FindReviewResponse(
                dto.getId(),
                WriterResponse.from(dto.getWriter(), loginMemberId),
                PlaceResponse.from(dto.getPlace()),
                dto.getKeywords().stream()
                        .map(ReviewKeywordValue::getContent)
                        .toList(),
                dto.getContent(),
                dto.getReviewImageDtos().stream()
                        .map(ReviewImageResponse::from)
                        .toList()
        );
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class WriterResponse {

        @Schema(description = "PK of member", example = "1")
        private Long id;

        @Schema(description = "로그인한 사용자가 리뷰 작성자와 동일한지", example = "false")
        private Boolean isEqualLoginMember;

        @Schema(description = "회원 프로필 썸네일 이미지 url", example = "https://member-profile-thumbnail-image-url")
        private String profileThumbnailImageUrl;

        @Schema(description = "닉네임", example = "우기")
        private String nickname;

        private static WriterResponse from(MemberDto dto, long loginMemberId) {
            return new WriterResponse(
                    dto.getId(),
                    loginMemberId == dto.getId(),
                    dto.getProfileThumbnailImageUrl(),
                    dto.getNickname()
            );
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class PlaceResponse {

        @Schema(description = "장소의 id(PK)", example = "1")
        private Long id;

        @Schema(description = "이름", example = "연남토마 본점")
        private String name;

        @Schema(description = "장소 카테고리", example = "퓨전일식")
        private String category;

        @Schema(description = "전화번호 (nullable)", example = "02-123-4567")
        private String phone;

        @Schema(description = "주소")
        private Address address;

        @Schema(description = "인스타그램 url (nullable)", example = "www.instagram.com/toma_wv/")
        private String snsUrl;

        @Schema(description = "좌표 정보")
        private Point point;

        @Schema(description = "휴무일 (nullable)", example = "일요일")
        private String closingHours;

        @Schema(description = "영업시간 정보",
                example = """
                        [
                            "월 11:30-22:00",
                            "화 11:30-22:00",
                            "수 11:30-22:00",
                            "목 11:30-22:00",
                            "금 11:30-22:00"
                        ]
                        """)
        private List<String> openingHoursDtos;

        @Schema(description = "장소에 대한 북마크 여부", example = "false")
        private Boolean isMarked;

        private static PlaceResponse from(PlaceDto dto) {
            String snsUrl = dto.getHomepageUrl();
            if (snsUrl != null && !snsUrl.contains("instagram")) {
                snsUrl = null;
            }

            return new PlaceResponse(
                    dto.getId(),
                    dto.getName(),
                    dto.getCategory().getSecondCategory() != null
                            ? dto.getCategory().getSecondCategory()
                            : dto.getCategory().getFirstCategory(),
                    dto.getPhone(),
                    dto.getAddress(),
                    snsUrl,
                    dto.getPoint(),
                    dto.getClosingHours(),
                    dto.getOpeningHoursDtos().stream()
                            .map(oh -> String.format(oh.getDayOfWeek().getDescription() + " " + oh.getOpenAt() + "-" + oh.getCloseAt()))
                            .toList(),
                    dto.getIsMarked()
            );
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class ReviewImageResponse {

        @Schema(description = "이미지 url", example = "https://review-image-url")
        private String imageUrl;

        @Schema(description = "썸네일 이미지 url", example = "https://review-thumbnail-image-url")
        private String thumbnailImageUrl;

        @Schema(description = "이미지에 첨부된 메뉴 태그 목록 (nullable)")
        private List<ReviewImageMenuTagResponse> menuTags;

        private static ReviewImageResponse from(ReviewImageDto dto) {
            List<ReviewImageMenuTagResponse> menuTagResponses = null;
            if (dto.getMenuTags() != null) {
                menuTagResponses = dto.getMenuTags().stream()
                        .map(ReviewImageMenuTagResponse::from)
                        .toList();
            }

            return new ReviewImageResponse(
                    dto.getUrl(),
                    dto.getThumbnailUrl(),
                    menuTagResponses
            );
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class ReviewImageMenuTagResponse {

        @Schema(description = "메뉴 이름", example = "떡볶이")
        private String content;

        @Schema(description = "메뉴 태그의 좌표 정보")
        private MenuTagPoint point;

        private static ReviewImageMenuTagResponse from(ReviewImageMenuTagDto dto) {
            return new ReviewImageMenuTagResponse(dto.getContent(), dto.getPoint());
        }
    }
}
