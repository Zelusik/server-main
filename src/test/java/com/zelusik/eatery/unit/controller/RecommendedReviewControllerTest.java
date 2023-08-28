package com.zelusik.eatery.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.constant.member.RoleType;
import com.zelusik.eatery.controller.RecommendedReviewController;
import com.zelusik.eatery.dto.recommended_review.RecommendedReviewDto;
import com.zelusik.eatery.dto.recommended_review.request.SaveRecommendedReviewsRequest;
import com.zelusik.eatery.security.UserPrincipal;
import com.zelusik.eatery.service.RecommendedReviewService;
import com.zelusik.eatery.util.MemberTestUtils;
import com.zelusik.eatery.util.RecommendedReviewTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @DisplayName("리뷰 id와 순위가 주어지고, 추천 리뷰를 등록하면, 422 에러가 발생한다.")
    @Test
    void givenReviewIdAndRanking_whenSavingRecommendedReview_thenResponse422Error() throws Exception {
        // given
        long memberId = 1L;
        long reviewId = 2L;
        short ranking = 3;
        SaveRecommendedReviewsRequest request = new SaveRecommendedReviewsRequest(reviewId, ranking);
        RecommendedReviewDto expectedResult = RecommendedReviewTestUtils.createRecommendedReviewDto(3L, memberId, reviewId, ranking);
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
                .andExpect(jsonPath("$.reviewId").value(expectedResult.getReviewId()))
                .andExpect(jsonPath("$.ranking").value(String.valueOf(expectedResult.getRanking())))
                .andDo(print());
    }

    @DisplayName("허용된 범위에서 벗어난 순위가 주어지고, 주어진 리뷰를 추천 리뷰로 등록한다.")
    @Test
    void givenRankingOutOfBounds_whenSavingRecommendedReview_thenSavesRecommendedReview() throws Exception {
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

    private UserDetails createTestUser(long memberId) {
        return UserPrincipal.of(MemberTestUtils.createMemberDtoWithId(memberId, Set.of(RoleType.USER)));
    }
}