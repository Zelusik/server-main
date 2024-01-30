package com.zelusik.eatery.domain.report_place.dto.response;

import com.zelusik.eatery.domain.report_place.dto.ReportPlaceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PostReportPlaceResponse {

    @Schema(description = "장소 신고의 id(PK)", example = "19")
    private Long id;

    @Schema(description = "장소를 신고한 회원의 id(PK)", example = "15")
    private Long reporterId;

    @Schema(description = "신고한 장소의 id(PK)", example = "2")
    private Long placeId;

    @Schema(description = "신고 이유 옵션 전체 문장", example = "전화번호")
    private String reasonOption;

    @Schema(description = "신고 이유 상세", example = "전화번호가 ~~로 변경되었습니다.")
    private String reasonDetail;

    public static PostReportPlaceResponse from(ReportPlaceDto reportPlaceDto) {
        return new PostReportPlaceResponse(
                reportPlaceDto.getId(),
                reportPlaceDto.getReporterId(),
                reportPlaceDto.getPlace().getId(),
                reportPlaceDto.getReasonOption().getFullSentence(),
                reportPlaceDto.getReasonDetail()
        );
    }
}