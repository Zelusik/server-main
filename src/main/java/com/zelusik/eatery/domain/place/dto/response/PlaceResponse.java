package com.zelusik.eatery.domain.place.dto.response;

import com.zelusik.eatery.domain.place.dto.PlaceWithMarkedStatusDto;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceResponse {

    @Schema(description = "장소의 id(PK)", example = "1")
    private Long id;

    @Schema(description = "<p>가장 많이 태그된 top 3 keywords." +
                          "<p>이 장소에 대한 리뷰가 없다면, empty array로 응답합니다.",
            example = "[\"신선한 재료\", \"최고의 맛\"]")
    List<String> top3Keywords;

    @Schema(description = "이름", example = "연남토마 본점")
    private String name;

    @Schema(description = "카테고리", example = "퓨전일식")
    private String category;

    @Schema(description = "대표번호", example = "02-332-8064")
    private String phone;

    @Schema(description = "주소")
    private Address address;

    @Schema(description = "인스타그램 url", example = "www.instagram.com/toma_wv/")
    private String snsUrl;

    @Schema(description = "좌표")
    private Point point;

    @Schema(description = "휴무일", example = "금요일")
    private String closingHours;

    @Schema(description = "영업시간 정보", example = """
            [
                "월 11:30-22:00",
                "화 11:30-22:00",
                "수 11:30-22:00",
                "목 11:30-22:00",
                "금 11:30-22:00"
            ]
            """)
    private List<String> openingHours;

    @Schema(description = "북마크 여부", example = "false")
    private Boolean isMarked;

    public static PlaceResponse of(Long id, List<String> top3Keywords, String name, String category, String phone, Address address, String snsUrl, Point point, String closingHours, List<String> openingHours, Boolean isMarked) {
        return new PlaceResponse(id, top3Keywords, name, category, phone, address, snsUrl, point, closingHours, openingHours, isMarked);
    }

    public static PlaceResponse from(PlaceWithMarkedStatusDto dto) {
        String snsUrl = dto.getHomepageUrl();
        if (!StringUtils.hasText(snsUrl) || !snsUrl.contains("instagram")) {
            snsUrl = null;
        }

        String category = dto.getCategory().getSecondCategory();
        if (category == null) {
            category = dto.getCategory().getFirstCategory();
        }

        List<String> openingHours = dto.getOpeningHours().stream()
                .map(oh -> String.format(oh.getDayOfWeek().getDescription() + " " + oh.getOpenAt() + "-" + oh.getCloseAt()))
                .toList();

        return new PlaceResponse(
                dto.getId(),
                dto.getTop3Keywords().stream()
                        .map(ReviewKeywordValue::getContent)
                        .toList(),
                dto.getName(),
                category,
                dto.getPhone(),
                dto.getAddress(),
                snsUrl,
                dto.getPoint(),
                dto.getClosingHours(),
                openingHours,
                dto.getIsMarked()
        );
    }
}
