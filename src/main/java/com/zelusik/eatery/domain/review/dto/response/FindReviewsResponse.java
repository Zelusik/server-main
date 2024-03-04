package com.zelusik.eatery.domain.review.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.place.dto.PlaceWithMarkedStatusDto;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review.dto.ReviewWithPlaceMarkedStatusDto;
import com.zelusik.eatery.domain.review_image.dto.ReviewImageDto;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class FindReviewsResponse {

    @Schema(description = "리뷰 id(PK)", example = "1")
    private Long id;

    @Schema(description = "작성자 정보")
    private WriterResponse writer;

    @Schema(description = "장소 정보")
    private PlaceResponse place;

    @Schema(description = "리뷰 키워드 목록", example = "[\"신선한 재료\", \"왁자지껄한\"]")
    private List<String> keywords;

    @Schema(description = "내용", example = "review content")
    private String content;

    @Schema(description = "리뷰에 첨부된 썸네일 이미지 url")
    private List<String> reviewThumbnailImageUrls;

    @Schema(description = "리뷰 생성 시각")
    private LocalDateTime createdAt;

    public static FindReviewsResponse from(ReviewWithPlaceMarkedStatusDto dto) {
        return new FindReviewsResponse(
                dto.getId(),
                dto.getWriter() != null ? WriterResponse.from(dto.getWriter()) : null,
                dto.getPlace() != null ? PlaceResponse.from(dto.getPlace()) : null,
                dto.getKeywords().stream().map(ReviewKeywordValue::getContent).toList(),
                dto.getContent(),
                dto.getReviewImageDtos().stream().map(ReviewImageDto::getThumbnailUrl).toList(),
                dto.getCreatedAt()
        );
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class WriterResponse {

        @Schema(description = "PK of member", example = "1")
        private Long id;

        @Schema(description = "회원 프로필 썸네일 이미지 url", example = "https://member-profile-thumbnail-image-url")
        private String profileThumbnailImageUrl;

        @Schema(description = "닉네임", example = "우기")
        private String nickname;

        private static WriterResponse from(MemberDto dto) {
            return new WriterResponse(dto.getId(), dto.getProfileThumbnailImageUrl(), dto.getNickname());
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

        @Schema(description = "음식 카테고리", example = "한식")
        private String category;

        @Schema(description = "주소")
        private Address address;

        @Schema(description = "장소에 대한 북마크 여부", example = "false")
        private Boolean isMarked;

        private static PlaceResponse from(PlaceWithMarkedStatusDto dto) {
            return new PlaceResponse(
                    dto.getId(),
                    dto.getName(),
                    FoodCategoryValue.valueOfFirstCategory(dto.getCategory().getFirstCategory()).getCategoryName(),
                    dto.getAddress(),
                    dto.getIsMarked()
            );
        }
    }
}
