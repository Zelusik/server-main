package com.zelusik.eatery.domain.report_place.service;

import com.zelusik.eatery.domain.report_place.dto.ReportPlaceDto;
import com.zelusik.eatery.domain.report_place.entity.ReportPlace;
import com.zelusik.eatery.domain.report_place.exception.ReportPlaceNotFoundByIdException;
import com.zelusik.eatery.domain.report_place.repository.ReportPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReportPlaceQueryService {

    private final ReportPlaceRepository reportPlaceRepository;

    /**
     * reportPlaceId에 해당하는 장소 신고 내역을 조회한다.
     *
     * @param reportPlaceId 조회하고자 하는 장소 신고 내역의 PK
     * @return 조회된 장소 신고 내역의 dto
     */
    public ReportPlaceDto getDtoByReportPlaceId(Long reportPlaceId) {
        ReportPlace reportPlace = reportPlaceRepository.findById(reportPlaceId)
                .orElseThrow(() -> new ReportPlaceNotFoundByIdException(reportPlaceId));
        return ReportPlaceDto.from(reportPlace);
    }
}
