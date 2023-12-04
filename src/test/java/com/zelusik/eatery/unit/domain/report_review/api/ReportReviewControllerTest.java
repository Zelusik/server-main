package com.zelusik.eatery.unit.domain.report_review.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.dto.PlaceWithMarkedStatusDto;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.report_review.api.ReportReviewControllerV1;
import com.zelusik.eatery.domain.report_review.dto.ReportReviewDto;
import com.zelusik.eatery.domain.report_review.dto.ReportReviewReasonOption;
import com.zelusik.eatery.domain.report_review.dto.request.ReportReviewRequest;
import com.zelusik.eatery.domain.report_review.service.ReportReviewCommandService;
import com.zelusik.eatery.domain.report_review.service.ReportReviewQueryService;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review.dto.ReviewDto;
import com.zelusik.eatery.domain.review_image.dto.ReviewImageDto;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.zelusik.eatery.global.common.constant.EateryConstants.API_MINOR_VERSION_HEADER_NAME;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Unit] Controller - Report Review")
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = ReportReviewControllerV1.class)
public class ReportReviewControllerTest {

    @MockBean
    private ReportReviewCommandService reportReviewCommandService;
    @MockBean
    private ReportReviewQueryService reportReviewQueryService;

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @Autowired
    public ReportReviewControllerTest(MockMvc mvc, ObjectMapper mapper) {
        this.mvc = mvc;
        this.mapper = mapper;
    }

    @DisplayName("리뷰 id와 신고 이유가 주어지고, 리뷰를 신고한다.")
    @Test
    void givenReviewId_whenReportingReview_thenReportReview() throws Exception {
        // given
        long reporterId = 1L;
        long reviewId = 2L;
        long placeId = 3L;
        String reasonOption = "ETC";
        String reasonDetail = "제가 리뷰로 올린 사진을 도용하였어요.";

        ReportReviewRequest request = new ReportReviewRequest(reviewId, reasonOption, reasonDetail);
        ReportReviewDto expectedResult = createReportReviewDto(4L, reporterId, createReviewDto(reviewId, createWriter(3L), createPlaceWithMarkedStatusDto(placeId)), ReportReviewReasonOption.valueOf(reasonOption), reasonDetail);
        given(reportReviewCommandService.reportReview(reporterId, reviewId, reasonOption, reasonDetail)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        post("/api/v1/reports/reviews")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request))
                                .with(user(createTestUser(reporterId)))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedResult.getId()))
                .andExpect(jsonPath("$.reporterId").value(expectedResult.getReporterId()))
                .andExpect(jsonPath("$.reviewId").value(expectedResult.getReview().getId()))
                .andExpect(jsonPath("$.reasonOption").value(expectedResult.getReasonOption().getFullSentence()))
                .andExpect(jsonPath("$.reasonDetail").value(expectedResult.getReasonDetail()))
                .andDo(print());
    }

    @DisplayName("id가 주어지고, 리뷰 신고 내역을 조회한다.")
    @Test
    void given_whenFindingReportReviewWithId_thenReturnReportReview() throws Exception {
        // given
        long id = 1L;
        long reporterId = 2L;
        long reviewId = 3L;
        long placeId = 4L;
        String reasonOption = "ETC";
        String reasonDetail = "제가 리뷰로 올린 사진을 도용하였어요.";

        ReportReviewDto expectedResult = createReportReviewDto(1L, reporterId, createReviewDto(reviewId, createWriter(3L), createPlaceWithMarkedStatusDto(placeId)), ReportReviewReasonOption.valueOf(reasonOption), reasonDetail);
        given(reportReviewQueryService.findDtoByReportReviewId(id)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/v1/reports/reviews/" + id)
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .with(user(createTestUser(1L)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResult.getId()))
                .andExpect(jsonPath("$.reporterId").value(expectedResult.getReporterId()))
                .andExpect(jsonPath("$.reviewId").value(expectedResult.getReview().getId()))
                .andExpect(jsonPath("$.reasonOption").value(expectedResult.getReasonOption().getFullSentence()))
                .andExpect(jsonPath("$.reasonDetail").value(expectedResult.getReasonDetail()))
        ;
    }

    private ReportReviewDto createReportReviewDto(long id, long reporterId, ReviewDto review, ReportReviewReasonOption reasonOption, String reasonDetail) {
        return new ReportReviewDto(id, reporterId, review, reasonOption, reasonDetail);
    }

    private UserDetails createTestUser(long memberId) {
        return UserPrincipal.of(createMemberDto(memberId, Set.of(RoleType.USER)));
    }

    private MemberDto createWriter(Long memberId) {
        return createMemberDto(memberId, Set.of(RoleType.USER));
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

    private PlaceWithMarkedStatusDto createPlaceWithMarkedStatusDto(long placeId) {
        return new PlaceWithMarkedStatusDto(
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
                List.of(),
                false
        );
    }

    private ReviewDto createReviewDto(long reviewId, MemberDto writer, PlaceWithMarkedStatusDto place) {
        return new ReviewDto(
                reviewId,
                writer,
                place,
                List.of(ReviewKeywordValue.NOISY, ReviewKeywordValue.FRESH),
                "자동 생성된 내용",
                "제출된 내용",
                List.of(createReviewImageDto(100L, reviewId)),
                LocalDateTime.now()
        );
    }

    private ReviewImageDto createReviewImageDto(long reviewImageId, long reviewId) {
        return new ReviewImageDto(
                reviewImageId,
                reviewId,
                "test.txt",
                "storedName",
                "url",
                "thumbnailStoredName",
                "thumbnailUrl"
        );
    }
}
