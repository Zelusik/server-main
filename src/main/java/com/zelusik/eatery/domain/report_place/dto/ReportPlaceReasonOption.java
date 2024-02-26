package com.zelusik.eatery.domain.report_place.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReportPlaceReasonOption {
    POSITION("음식점의 위치"),
    TIME("운영 시간"),
    CLOSED_DAYS("휴무일 정보"),
    NUMBER("전화번호"),
    SNS("sns 정보"),
    ETC("기타");

    private final String fullSentence;
}
