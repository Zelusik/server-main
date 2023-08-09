package com.zelusik.eatery.unit.controller;

import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.controller.ReviewController;
import com.zelusik.eatery.dto.review.request.ReviewCreateRequest;
import com.zelusik.eatery.security.UserPrincipal;
import com.zelusik.eatery.service.OpenAIService;
import com.zelusik.eatery.service.ReviewService;
import com.zelusik.eatery.util.MemberTestUtils;
import com.zelusik.eatery.util.MultipartFileTestUtils;
import com.zelusik.eatery.util.ReviewTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Unit] Review Controller")
@MockBean(JpaMetamodelMappingContext.class)
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = ReviewController.class)
class ReviewControllerTest {

    @MockBean
    ReviewService reviewService;
    @MockBean
    OpenAIService openAIService;

    private final MockMvc mvc;

    public ReviewControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("생성할 리뷰 정보가 주어지고, 리뷰를 생성하면, 생성 후 저장된 리뷰가 반환된다.")
    @Test
    void givenReviewInfo_whenReviewCreate_thenReturnSavedReview() throws Exception {
        // given
        long placeId = 3L;
        given(reviewService.create(any(), any(ReviewCreateRequest.class))).willReturn(ReviewTestUtils.createReviewDto());

        // when & then
        ReviewCreateRequest reviewCreateRequest = ReviewTestUtils.createReviewCreateRequest(placeId);
        mvc.perform(
                        multipart("/api/reviews")
                                .file(MultipartFileTestUtils.createMockMultipartFile())
                                .param("placeId", String.valueOf(placeId))
                                .param("keywords", "신선한 재료", "왁자지껄한")
                                .param("autoCreatedContent", reviewCreateRequest.getAutoCreatedContent())
                                .param("content", reviewCreateRequest.getContent())
                                .with(user(createTestUserDetails()))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @DisplayName("가게의 id(PK)가 주어지고, 특정 가게에 대한 리뷰 목록을 조회하면, 조회된 리뷰 목록(Slice)을 반환한다.")
    @Test
    void givenPlaceId_whenSearchReviewsOfCertainPlace_thenReturnReviews() throws Exception {
        // given
        long placeId = 1L;
        given(reviewService.findDtosByPlaceId(eq(placeId), any(Pageable.class)))
                .willReturn(new SliceImpl<>(List.of(ReviewTestUtils.createReviewDtoWithoutPlace())));

        // when & then
        mvc.perform(
                        get("/api/reviews?placeId=" + placeId)
                                .with(user(UserPrincipal.of(MemberTestUtils.createMemberDtoWithId())))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasContent").value(true));
    }

    @DisplayName("내가 작성한 리뷰 목록을 조회하면, 조회된 리뷰 목록(Slice)을 반환한다.")
    @Test
    void whenSearchMyReviews_thenReturnReviews() throws Exception {
        // given
        long memberId = 1L;
        given(reviewService.findDtosByWriterId(eq(memberId), any(Pageable.class)))
                .willReturn(new SliceImpl<>(List.of(ReviewTestUtils.createReviewDto())));

        // when & then
        mvc.perform(
                        get("/api/reviews/me")
                                .with(user(createTestUserDetails()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasContent").value(true));
    }

    @DisplayName("리뷰를 작성하고자 하는 장소에 대한 키워드들과 메뉴 목록 및 각 메뉴에 대한 키워드들이 주어지고, 자동 생성된 리뷰 내용을 조회하면, 생성된 리뷰 내용을 반환한다.")
    @Test
    void givenPlaceKeywordsAndMenusAndMenuKeywords_whenGettingAutoCreatedReviewContent_thenReturnRespondedMessageContent() throws Exception {
        // given
        List<String> placeKeywords = List.of("신선한 재료", "넉넉한 양", "술과 함께", "데이트에 최고");
        List<String> menus = List.of("시금치카츠카레", "버터치킨카레");
        List<String> menuKeywords = List.of("싱그러운+육즙 가득힌+매콤한", "부드러운+촉촉한");

        String expectedResult = "생성된 리뷰 내용";
        given(openAIService.getAutoCreatedReviewContent(
                placeKeywords,
                menus,
                menuKeywords.stream()
                        .map(keywords -> Arrays.asList(keywords.split("/+")))
                        .toList()
        )).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/reviews/contents/auto-creations")
                                .param("placeKeywords", placeKeywords.toArray(new String[0]))
                                .param("menus", menus.toArray(new String[0]))
                                .param("menuKeywords", menuKeywords.toArray(new String[0]))
                                .with(user(createTestUserDetails()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(expectedResult))
                .andDo(print());
    }

    @DisplayName("리뷰 내용 자동 생성 시, 주어진 메뉴와 메뉴 키워드의 개수가 맞지 않다면, 예외가 발생한다.")
    @Test
    void givenMenusAndKeywordsWithMismatchedCounts_whenGettingAutoCreatedContent_thenThrowMismatchedMenuKeywordCountException() throws Exception {
        // given
        List<String> placeKeywords = List.of("신선한 재료", "넉넉한 양");
        List<String> menus = List.of("시금치카츠카레", "버터치킨카레");
        List<String> menuKeywords = List.of("싱그러운+육즙 가득힌+매콤한");

        // when & then
        mvc.perform(
                        get("/api/reviews/contents/auto-creations")
                                .param("placeKeywords", placeKeywords.toArray(new String[0]))
                                .param("menus", menus.toArray(new String[0]))
                                .param("menuKeywords", menuKeywords.toArray(new String[0]))
                                .with(user(createTestUserDetails()))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    private UserDetails createTestUserDetails() {
        return UserPrincipal.of(MemberTestUtils.createMemberDtoWithId());
    }
}