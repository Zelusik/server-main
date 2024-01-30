package com.zelusik.eatery.domain.report_place.dto;

import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.report_place.entity.ReportPlace;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReportPlaceDto {

    private Long id;
    private Long reporterId;
    private PlaceDto place;
    private ReportPlaceReasonOption reasonOption;
    private String reasonDetail;

    public static ReportPlaceDto from(ReportPlace entity) {
        return new ReportPlaceDto(
                entity.getId(),
                entity.getReporter().getId(),
                PlaceDto.from(entity.getPlace()),
                entity.getReasonOption(),
                entity.getReasonDetail()
        );
    }
}

