package com.zelusik.eatery.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.constant.ConstantUtil;
import com.zelusik.eatery.constant.FoodCategoryValue;
import com.zelusik.eatery.constant.member.Gender;
import com.zelusik.eatery.constant.member.LoginType;
import com.zelusik.eatery.constant.member.RoleType;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.controller.RecommendedReviewController;
import com.zelusik.eatery.dto.member.MemberDto;
import com.zelusik.eatery.dto.recommended_review.RecommendedReviewDto;
import com.zelusik.eatery.dto.recommended_review.request.BatchUpdateRecommendedReviewsRequest;
import com.zelusik.eatery.dto.recommended_review.request.SaveRecommendedReviewsRequest;
import com.zelusik.eatery.dto.review.ReviewDto;
import com.zelusik.eatery.dto.review.ReviewImageDto;
import com.zelusik.eatery.security.UserPrincipal;
import com.zelusik.eatery.service.RecommendedReviewService;
import com.zelusik.eatery.util.PlaceTestUtils;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Unit] Recommended Review Controller")
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = RecommendedReviewController.class)
class RecommendedReviewControllerTest {

    @MockBean
    private RecommendedReviewService recommendedReviewService;

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @Autowired
    public RecommendedReviewControllerTest(MockMvc mvc, ObjectMapper mapper) {
        this.mvc = mvc;
        this.mapper = mapper;
    }

    @DisplayName("리뷰 id와 순위가 주어지고, 추천 리뷰를 등록한다.")
    @Test
    void givenReviewIdAndRanking_whenSavingRecommendedReview_thenSavesRecommendedReview() throws Exception {
        // given
        long memberId = 1L;
        long reviewId = 2L;
        short ranking = 3;
        SaveRecommendedReviewsRequest request = new SaveRecommendedReviewsRequest(reviewId, ranking);
        RecommendedReviewDto expectedResult = createRecommendedReviewDto(3L, memberId, reviewId, ranking);
        given(recommendedReviewService.saveRecommendedReview(memberId, reviewId, ranking)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        post("/api/members/recommended-reviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request))
                                .with(user(createTestUser(memberId)))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedResult.getId()))
                .andExpect(jsonPath("$.reviewId").value(expectedResult.getReview().getId()))
                .andExpect(jsonPath("$.ranking").value(String.valueOf(expectedResult.getRanking())))
                .andDo(print());
    }

    @DisplayName("허용된 범위에서 벗어난 순위가 주어지고, 주어진 리뷰를 추천 리뷰로 등록하면, validation 에러가 발생한다.")
    @Test
    void givenRankingOutOfBounds_whenSavingRecommendedReview_thenResponseValidationError() throws Exception {
        // given
        long memberId = 1L;
        long reviewId = 2L;
        short ranking = 4;
        SaveRecommendedReviewsRequest request = new SaveRecommendedReviewsRequest(reviewId, ranking);

        // when & then
        mvc.perform(
                        post("/api/members/recommended-reviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request))
                                .with(user(createTestUser(memberId)))
                )
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @DisplayName("회원 id가 주어지고, id에 해당하는 회원이 설정한 추천 리뷰들을 함께 조회한다.")
    @Test
    void given_whenFindingRecommendedReviewsWithMemberId_thenReturnRecommendedReviews() throws Exception {
        // given
        long memberId = 2L;
        long recommendedReviewId = 3L;
        short ranking = 5;
        List<RecommendedReviewDto> expectedResults = List.of(createRecommendedReviewDto(recommendedReviewId, memberId, 4L, ranking));
        given(recommendedReviewService.findAllDtosWithPlaceMarkedStatus(memberId)).willReturn(expectedResults);

        // when & then
        mvc.perform(
                        get("/api/members/" + memberId + "/recommended-reviews")
                                .with(user(createTestUser(1L)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendedReviews", hasSize(expectedResults.size())))
                .andExpect(jsonPath("$.recommendedReviews[0].id").value(recommendedReviewId))
                .andExpect(jsonPath("$.recommendedReviews[0].ranking").value(String.valueOf(ranking)));
    }

    @DisplayName("새로 갱신하고자 하는 추천 리뷰 세 개의 정보가 주어지고, 추천 리뷰를 batch update하면, 추천 리뷰가 전달받은 리뷰로 갱신된다.")
    @Test
    void givenNewRecommendedReviewInfos_whenBatchUpdateRecommendedReviews_thenUpdateRecommendedReviews() throws Exception {
        // given
        long memberId = 1L;
        BatchUpdateRecommendedReviewsRequest batchUpdateRecommendedReviewsRequest = new BatchUpdateRecommendedReviewsRequest(List.of(
                new BatchUpdateRecommendedReviewsRequest.RecommendedReviewRequest(2L, (short) 1),
                new BatchUpdateRecommendedReviewsRequest.RecommendedReviewRequest(3L, (short) 2),
                new BatchUpdateRecommendedReviewsRequest.RecommendedReviewRequest(4L, (short) 3)
        ));
        List<RecommendedReviewDto> expectedResults = List.of(
                createRecommendedReviewDto(5L, memberId, 2L, (short) 1),
                createRecommendedReviewDto(6L, memberId, 3L, (short) 2),
                createRecommendedReviewDto(7L, memberId, 4L, (short) 3)
        );
        given(recommendedReviewService.batchUpdateRecommendedReviews(eq(memberId), any(BatchUpdateRecommendedReviewsRequest.class))).willReturn(expectedResults);

        // when & then
        mvc.perform(
                        put("/api/members/recommended-reviews/batch-update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(batchUpdateRecommendedReviewsRequest))
                                .with(user(createTestUser(memberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendedReviews", hasSize(expectedResults.size())))
                .andDo(print());
    }

    @DisplayName("크기가 3이 아닌 추천 리뷰 목록 정보가 주어지고, 추천 리뷰 목록을 batch update 하려고 하면, validation 에러가 발생한다.")
    @Test
    void givenNewRecommendedReviewInfoThatSizeIsNotThree_whenBatchUpdateRecommendedReviews_thenResponseValidationError() throws Exception {
        // given
        long memberId = 1L;
        BatchUpdateRecommendedReviewsRequest batchUpdateRecommendedReviewsRequest = new BatchUpdateRecommendedReviewsRequest(List.of(
                new BatchUpdateRecommendedReviewsRequest.RecommendedReviewRequest(2L, (short) 1)
        ));

        // when & then
        mvc.perform(
                        put("/api/members/recommended-reviews/batch-update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(batchUpdateRecommendedReviewsRequest))
                                .with(user(createTestUser(memberId)))
                )
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    private UserDetails createTestUser(long memberId) {
        return UserPrincipal.of(createMemberDto(memberId, Set.of(RoleType.USER)));
    }

    private MemberDto createMemberDto(Long memberId) {
        return createMemberDto(memberId, Set.of(RoleType.USER));
    }

    private MemberDto createMemberDto(long memberId, Set<RoleType> roleTypes) {
        return new MemberDto(
                memberId,
                null,
                ConstantUtil.defaultProfileImageUrl,
                ConstantUtil.defaultProfileThumbnailImageUrl,
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

    private RecommendedReviewDto createRecommendedReviewDto(long id, long memberId, long reviewId, short ranking) {
        return new RecommendedReviewDto(
                id,
                memberId,
                createReviewDto(reviewId, createMemberDto(memberId)),
                ranking
        );
    }

    private ReviewDto createReviewDto(long reviewId, MemberDto writer) {
        return new ReviewDto(
                reviewId,
                writer,
                PlaceTestUtils.createPlaceDto(),
                List.of(ReviewKeywordValue.NOISY, ReviewKeywordValue.FRESH),
                "자동 생성된 내용",
                "제출된 내용",
                List.of(new ReviewImageDto(
                        1L,
                        1L,
                        "test.txt",
                        "storedName",
                        "url",
                        "thumbnailStoredName",
                        "thumbnailUrl"))
        );
    }
}