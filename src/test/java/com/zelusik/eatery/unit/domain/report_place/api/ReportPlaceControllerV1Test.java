package com.zelusik.eatery.unit.domain.report_place.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.report_place.api.ReportPlaceControllerV1;
import com.zelusik.eatery.domain.report_place.dto.ReportPlaceDto;
import com.zelusik.eatery.domain.report_place.dto.ReportPlaceReasonOption;
import com.zelusik.eatery.domain.report_place.dto.request.ReportPlaceRequest;
import com.zelusik.eatery.domain.report_place.service.ReportPlaceCommandService;
import com.zelusik.eatery.domain.report_place.service.ReportPlaceQueryService;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.global.auth.UserPrincipal;
import com.zelusik.eatery.global.common.constant.EateryConstants;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.zelusik.eatery.global.common.constant.EateryConstants.API_MINOR_VERSION_HEADER_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Unit] Controller - Report Place")
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = ReportPlaceControllerV1.class)
public class ReportPlaceControllerV1Test {

    @MockBean
    private ReportPlaceCommandService reportPlaceCommandService;
    @MockBean
    private ReportPlaceQueryService reportPlaceQueryService;
    @MockBean
    ReportPlaceReasonOption mockReasonOption;

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @Autowired
    public ReportPlaceControllerV1Test(MockMvc mvc, ObjectMapper mapper) {
        this.mvc = mvc;
        this.mapper = mapper;
    }

    @DisplayName("장소 id와 장소 정보 수정 제안 이유가 주어지고, 해당 장소를 신고한다.")
    @Test
    void givenPlaceId_whenReportPlaceCreate_thenReturnSavedReportPlace() throws Exception {
        // given
        long reporterId = 1L;
        long placeId = 2L;
        ReportPlaceReasonOption reasonOption = ReportPlaceReasonOption.NUMBER;
        String reasonDetail = "전화번호가 ~~로 수정되었어요.";

        ReportPlaceRequest request = new ReportPlaceRequest(placeId, reasonOption, reasonDetail);
        ReportPlaceDto expectedResult = createReportPlaceDto(1L, reporterId, createPlaceDto(placeId), reasonOption, reasonDetail);
        given(reportPlaceCommandService.reportPlace(eq(reporterId), any(ReportPlaceRequest.class))).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        post("/api/v1/reports/places")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request))
                                .with(user(createTestUser(reporterId)))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedResult.getId()))
                .andExpect(jsonPath("$.reporterId").value(expectedResult.getReporterId()))
                .andExpect(jsonPath("$.placeId").value(expectedResult.getPlace().getId()))
                .andExpect(jsonPath("$.reasonOption").value(expectedResult.getReasonOption().getFullSentence()))
                .andExpect(jsonPath("$.reasonDetail").value(expectedResult.getReasonDetail()))
                .andDo(print());
    }

    @DisplayName("id가 주어지고, 장소 신고 내역을 조회한다.")
    @Test
    void given_whenFindingReportPlaceWithId_thenReturnReportPlace() throws Exception {
        // given
        long id = 1L;
        long reporterId = 2L;
        long placeId = 3L;
        ReportPlaceReasonOption reasonOption = ReportPlaceReasonOption.NUMBER;
        String reasonDetail = "전화번호가 ~~로 수정되었어요.";

        ReportPlaceDto expectedResult = createReportPlaceDto(1L, reporterId, createPlaceDto(placeId), reasonOption, reasonDetail);
        given(reportPlaceQueryService.getDtoByReportPlaceId(id)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/v1/reports/places/" + id)
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .with(user(createTestUser(1L)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResult.getId()))
                .andExpect(jsonPath("$.reporterId").value(expectedResult.getReporterId()))
                .andExpect(jsonPath("$.placeId").value(expectedResult.getPlace().getId()))
                .andExpect(jsonPath("$.reasonOption").value(expectedResult.getReasonOption().getFullSentence()))
                .andExpect(jsonPath("$.reasonDetail").value(expectedResult.getReasonDetail()))
        ;
    }

    private ReportPlaceDto createReportPlaceDto(long id, long reporterId, PlaceDto place, ReportPlaceReasonOption reasonOption, String reasonDetail) {
        return new ReportPlaceDto(id, reporterId, place, reasonOption, reasonDetail);
    }

    private UserDetails createTestUser(long memberId) {
        return UserPrincipal.of(createMemberDto(memberId, Set.of(RoleType.USER)));
    }

    private MemberDto createMemberDto(long memberId, Set<RoleType> roleTypes) {
        return new MemberDto(
                memberId,
                EateryConstants.defaultProfileImageUrl,
                EateryConstants.defaultProfileThumbnailImageUrl,
                "1234567890",
                LoginType.KAKAO,
                roleTypes,
                "test@test.com",
                "test",
                LocalDate.of(1998, 1, 5),
                20,
                Gender.MALE,
                List.of(FoodCategoryValue.KOREAN),
                null
        );
    }

    private PlaceDto createPlaceDto(long placeId) {
        return new PlaceDto(
                placeId,
                List.of(ReviewKeywordValue.FRESH),
                "308342289",
                "연남토마 본점",
                "https://place.map.kakao.com/308342289",
                KakaoCategoryGroupCode.FD6,
                PlaceCategory.of("음식점 > 퓨전요리 > 퓨전일식"),
                "02-332-8064",
                Address.of("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"),
                "https://place.map.kakao.com/308342289",
                new Point("37.5595073462493", "126.921462488105"),
                null,
                List.of()
        );
    }
}
