package com.zelusik.eatery.unit.domain.report_place.service;

import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.report_place.dto.ReportPlaceDto;
import com.zelusik.eatery.domain.report_place.dto.ReportPlaceReasonOption;
import com.zelusik.eatery.domain.report_place.entity.ReportPlace;
import com.zelusik.eatery.domain.report_place.repository.ReportPlaceRepository;
import com.zelusik.eatery.domain.report_place.service.ReportPlaceQueryService;
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
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Service(Query) - Report place")
@ExtendWith(MockitoExtension.class)
class ReportPlaceQueryServiceTest {

    @InjectMocks
    private ReportPlaceQueryService sut;

    @Mock
    private ReportPlaceRepository reportPlaceRepository;

    @DisplayName("장소 신고 내역 id가 주어지고, 신고자 id, 장소 정보, 이유, 상세 이유를 조회한다.")
    @Test
    void given_whenFindingReportPlaceWithId_thenReturnReportPlace() {
        // given
        long reporterId = 1L;
        long placeId = 2L;
        long reportPlaceId = 3L;

        ReportPlace expectedResult = createReportPlace(reportPlaceId, createMember(reporterId), createPlace(placeId, "12345"), ReportPlaceReasonOption.NUMBER, "전화번호가 ~~로 수정되었어요.");
        given(reportPlaceRepository.findById(reportPlaceId)).willReturn(Optional.of(expectedResult));

        // when
        ReportPlaceDto actualResult = sut.getDtoById(reportPlaceId);

        // then
        then(reportPlaceRepository).should().findById(reportPlaceId);
        then(reportPlaceRepository).shouldHaveNoMoreInteractions();
        assertThat(actualResult)
                .hasFieldOrPropertyWithValue("id", expectedResult.getId())
                .hasFieldOrPropertyWithValue("reporterId", expectedResult.getReporter().getId())
                .hasFieldOrPropertyWithValue("place.id", expectedResult.getPlace().getId())
                .hasFieldOrPropertyWithValue("reasonOption", expectedResult.getReasonOption())
                .hasFieldOrPropertyWithValue("reasonDetail", expectedResult.getReasonDetail());
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