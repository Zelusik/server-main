package com.zelusik.eatery.domain.meeting_place.dto.response;

import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.global.kakao.dto.KakaoPlaceInfo;
import com.zelusik.eatery.domain.location.dto.LocationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MeetingPlaceResponse {

    @Schema(description = "장소의 이름", example = "홍대입구역 2호선")
    private String name;

    @Schema(description = "장소가 위치한 시/도", example = "서울")
    private String sido;

    @Schema(description = "장소가 위치한 시/군/구", example = "마포구")
    private String sgg;

    @Schema(description = "장소가 위치한 읍/면/동", example = "동교동")
    private String emd;

    @Schema(description = "좌표")
    private Point point;

    public static MeetingPlaceResponse from(LocationDto dto) {
        String name = dto.getSido();
        if (dto.getSgg() != null) {
            name = dto.getSgg();
        }
        if (dto.getEmdg() != null) {
            name = dto.getEmdg();
        }
        return new MeetingPlaceResponse(
                name,
                dto.getSido(),
                dto.getSgg(),
                dto.getEmdg(),
                dto.getPoint()
        );
    }

    public static MeetingPlaceResponse from(KakaoPlaceInfo response) {
        String address = response.getAddressName();

        int sidoIdx = address.indexOf(" ");
        int sggIdx = address.indexOf(" ", sidoIdx + 1);
        int emdIdx = address.indexOf(" ", sggIdx + 1);

        String sido = sidoIdx != -1 ? address.substring(0, sidoIdx) : null;
        String sgg = sggIdx != -1 ? address.substring(sidoIdx + 1, sggIdx) : null;
        String emd = emdIdx != -1 ? address.substring(sggIdx + 1, emdIdx) : null;

        return new MeetingPlaceResponse(
                response.getPlaceName(),
                sido,
                sgg,
                emd,
                new Point(response.getY(), response.getX())
        );
    }
}
