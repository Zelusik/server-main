package com.zelusik.eatery.domain.report_place.dto.request;

import com.zelusik.eatery.domain.report_place.dto.ReportPlaceReasonOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReportPlaceRequest {
    @Schema(description = "신고하고자 하는 장소의 id(PK)", example = "2")
    @NotNull
    private Long placeId;

    @Schema(description = "신고 이유 선택(POSITION, TIME, CLOSED_DAYS, NUMBER, SNS, ETC 중 택 1)", example = "NUMBER")
    @NotNull
    private ReportPlaceReasonOption reasonOption;

    @Schema(description = "신고 이유 상세", example = "전화번호가 ~~로 변경되었습니다.")
    @NotNull
    private String reasonDetail;
}
