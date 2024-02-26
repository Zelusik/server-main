package com.zelusik.eatery.unit.domain.report_place.service;

import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.member.service.MemberQueryService;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.place.service.PlaceQueryService;
import com.zelusik.eatery.domain.report_place.dto.ReportPlaceDto;
import com.zelusik.eatery.domain.report_place.dto.ReportPlaceReasonOption;
import com.zelusik.eatery.domain.report_place.dto.request.ReportPlaceRequest;
import com.zelusik.eatery.domain.report_place.entity.ReportPlace;
import com.zelusik.eatery.domain.report_place.repository.ReportPlaceRepository;
import com.zelusik.eatery.domain.report_place.service.ReportPlaceCommandService;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Service(Command) - Report place")
@ExtendWith(MockitoExtension.class)
class ReportPlaceCommandServiceTest {

    @InjectMocks
    private ReportPlaceCommandService sut;

    @Mock
    private MemberQueryService memberQueryService;
    @Mock
    private PlaceQueryService placeQueryService;
    @Mock
    private ReportPlaceRepository reportPlaceRepository;

    @DisplayName("신고자 id와 장소 id, 장소 신고 요청 이유가 주어지고, 주어진 장소를 신고한다.")
    @Test
    void givenReporterIdAndRequest_whenReportingPlace_thenSavesReportPlace() {
        // given
        long reporterId = 1L;
        long placeId = 2L;
        Member reporter = createMember(reporterId);
        Place place = createPlace(placeId, "12345");
        ReportPlaceReasonOption reasonOption = ReportPlaceReasonOption.NUMBER;
        String reasonDetail = "전화번호가 ~~로 수정되었어요.";
        ReportPlace reportPlace = createReportPlace(3L, reporter, place, reasonOption, reasonDetail);
        given(memberQueryService.getById(reporterId)).willReturn(reporter);
        given(placeQueryService.getById(placeId)).willReturn(place);
        given(reportPlaceRepository.save(any(ReportPlace.class))).willReturn(reportPlace);

        // when
        ReportPlaceDto actualResult = sut.reportPlace(reporterId, new ReportPlaceRequest(placeId, reasonOption, reasonDetail));

        // then
        then(memberQueryService).should().getById(reporterId);
        then(placeQueryService).should().getById(placeId);
        then(reportPlaceRepository).should().save(any(ReportPlace.class));
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult)
                .hasFieldOrPropertyWithValue("reporterId", reporter.getId())
                .hasFieldOrPropertyWithValue("place.id", place.getId());
    }

    private void verifyEveryMocksShouldHaveNoMoreInteractions() {
        then(memberQueryService).shouldHaveNoMoreInteractions();
        then(placeQueryService).shouldHaveNoMoreInteractions();
        then(reportPlaceRepository).shouldHaveNoMoreInteractions();
    }

    private Member createMember(Long memberId) {
        return createMember(memberId, Set.of(RoleType.USER));
    }

    private Member createMember(Long memberId, Set<RoleType> roleTypes) {
        return new Member(
                memberId,
                "profile image url",
                "profile thunmbnail image url",
                "social user id",
                LoginType.KAKAO,
                roleTypes,
                "email",
                "nickname",
                LocalDate.of(2000, 1, 1),
                20,
                Gender.MALE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private Place createPlace(long id, String kakaoPid) {
        return Place.of(
                id,
                List.of(ReviewKeywordValue.FRESH),
                kakaoPid,
                "place name",
                "page url",
                KakaoCategoryGroupCode.FD6,
                new PlaceCategory("한식", "냉면", null),
                null,
                new Address("sido", "sgg", "lot number address", "road address"),
                null,
                new Point("37.5595073462493", "126.921462488105"),
                "",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private ReportPlace createReportPlace(Long id, Member reporter, Place place, ReportPlaceReasonOption reasonOption, String reasonDetail) {
        return ReportPlace.create(
                id,
                reporter,
                place,
                reasonOption,
                reasonDetail,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}