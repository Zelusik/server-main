package com.zelusik.eatery.dto.place.response;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.place.Address;
import com.zelusik.eatery.dto.place.PlaceDto;
import com.zelusik.eatery.dto.review.ReviewImageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FindPlaceResponse {

    @Schema(description = "장소의 id(PK)", example = "1")
    private Long id;

    @Schema(description = "<p>가장 많이 태그된 top 3 keywords." +
                          "<p>이 장소에 대한 리뷰가 없다면, empty array로 응답합니다.",
            example = "[\"신선한 재료\", \"최고의 맛\"]")
    private List<String> top3Keywords;

    @Schema(description = "이름", example = "연남토마 본점")
    private String name;

    @Schema(description = "카테고리", example = "퓨전일식")
    private String category;

    @Schema(description = "가게 번호 (nullable)", example = "02-332-8064")
    private String phone;

    @Schema(description = "주소")
    private Address address;

    @Schema(description = "인스타그램 url (nullable)", example = "www.instagram.com/toma_wv/")
    private String snsUrl;

    @Schema(description = "휴무일 (nullable)", example = "금요일")
    private String closingHours;

    @Schema(description = "영업 시간", example = "[\"월 11:30-22:00\", \"화 11:30-22:00\", \"수 11:30-22:00\", \"목 11:30-22:00\", \"금 11:30-22:00\"]")
    private List<String> openingHours;

    @Schema(description = "<p>장소 대표 이미지 (최대 9개)" +
                          "<p>장소에 대한 이미지가 없는 경우, 즉 작성된 리뷰가 없는 경우 empty array로 응답.")
    private List<ImageResponse> placeImages;

    @Schema(description = "북마크 여부", example = "false")
    private Boolean isMarked;

    public static FindPlaceResponse from(PlaceDto dto) {
        String snsUrl = dto.getHomepageUrl();
        if (snsUrl != null && !snsUrl.contains("instagram")) {
            snsUrl = null;
        }

        List<ImageResponse> placeImages = List.of();
        if (dto.getImages() != null) {
            placeImages = dto.getImages().stream()
                    .map(ImageResponse::from)
                    .toList();
        }

        return new FindPlaceResponse(
                dto.getId(),
                dto.getTop3Keywords().stream()
                        .map(ReviewKeywordValue::getDescription)
                        .toList(),
                dto.getName(),
                dto.getCategory().getSecondCategory() != null
                        ? dto.getCategory().getSecondCategory()
                        : dto.getCategory().getFirstCategory(),
                dto.getPhone(),
                dto.getAddress(),
                snsUrl,
                dto.getClosingHours(),
                dto.getOpeningHoursDtos().stream()
                        .map(oh -> String.format(oh.getDayOfWeek().getDescription() + " " + oh.getOpenAt() + "-" + oh.getCloseAt()))
                        .toList(),
                placeImages,
                dto.getIsMarked()
        );
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static class ImageResponse {

        @Schema(description = "이미지 url", example = "https://image-url")
        private String imageUrl;

        @Schema(description = "썸네일 이미지 url", example = "https://thumbnail-image-url")
        private String thumbnailImageUrl;

        public static ImageResponse from(ReviewImageDto reviewImageDto) {
            return new ImageResponse(reviewImageDto.getUrl(), reviewImageDto.getThumbnailUrl());
        }
    }
}
