package com.zelusik.eatery.domain.report_place.service;

import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.member.service.MemberQueryService;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.service.PlaceQueryService;
import com.zelusik.eatery.domain.report_place.dto.ReportPlaceDto;
import com.zelusik.eatery.domain.report_place.dto.request.ReportPlaceRequest;
import com.zelusik.eatery.domain.report_place.entity.ReportPlace;
import com.zelusik.eatery.domain.report_place.repository.ReportPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ReportPlaceCommandService {
    private final MemberQueryService memberQueryService;
    private final PlaceQueryService placeQueryService;
    private final ReportPlaceRepository reportPlaceRepository;

    /**
     * 장소를 신고한다.
     *
     * @param reporterId login member Id
     * @param body       장소 신고 요청 request 객체
     * @return 장소 신고 dto
     */
    public ReportPlaceDto reportPlace(Long reporterId, ReportPlaceRequest body) {
        Member reporter = memberQueryService.getById(reporterId);
        Place place = placeQueryService.getById(body.getPlaceId());

        ReportPlace newReportPlace = ReportPlace.create(reporter, place, body.getReasonOption(), body.getReasonDetail());
        ReportPlace reportPlace = reportPlaceRepository.save(newReportPlace);
        return ReportPlaceDto.from(reportPlace);
    }
}
