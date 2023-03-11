package com.zelusik.eatery.app.controller;

import com.zelusik.eatery.app.config.SecurityConfig;
import com.zelusik.eatery.app.dto.review.request.ReviewCreateRequest;
import com.zelusik.eatery.app.service.ReviewService;
import com.zelusik.eatery.global.security.JwtAuthenticationFilter;
import com.zelusik.eatery.global.security.UserPrincipal;
import com.zelusik.eatery.util.MemberTestUtils;
import com.zelusik.eatery.util.MultipartFileTestUtils;
import com.zelusik.eatery.util.ReviewTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Controller] Review")
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(
        controllers = ReviewController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
class ReviewControllerTest {

    @MockBean
    ReviewService reviewService;

    private final MockMvc mvc;

    public ReviewControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("생성할 리뷰 정보가 주어지고, 리뷰를 생성하면, 생성 후 저장된 리뷰가 반환된다.")
    @Test
    void givenReviewInfo_whenReviewCreate_thenReturnSavedReview() throws Exception {
        // given
        given(reviewService.create(any(), any(ReviewCreateRequest.class), any()))
                .willReturn(ReviewTestUtils.createReviewDtoWithId());

        // when & then
        ReviewCreateRequest reviewCreateRequest = ReviewTestUtils.createReviewCreateRequest();
        mvc.perform(
                        multipart("/api/reviews")
                                .file(MultipartFileTestUtils.createMockMultipartFile())
                                .param("place.kakaoPid", reviewCreateRequest.getPlace().getKakaoPid())
                                .param("place.name", reviewCreateRequest.getPlace().getName())
                                .param("place.pageUrl", reviewCreateRequest.getPlace().getPageUrl())
                                .param("place.categoryGroupCode", reviewCreateRequest.getPlace().getCategoryGroupCode().toString())
                                .param("place.categoryName", reviewCreateRequest.getPlace().getCategoryName())
                                .param("place.phone", reviewCreateRequest.getPlace().getPhone())
                                .param("place.lotNumberAddress", reviewCreateRequest.getPlace().getLotNumberAddress())
                                .param("place.roadAddress", reviewCreateRequest.getPlace().getRoadAddress())
                                .param("place.lat", reviewCreateRequest.getPlace().getLat())
                                .param("place.lng", reviewCreateRequest.getPlace().getLng())
                                .param("keywords", "신선한 재료", "왁자지껄한")
                                .param("autoCreatedContent", reviewCreateRequest.getAutoCreatedContent())
                                .param("content", reviewCreateRequest.getContent())
                                .with(csrf())
                                .with(user(UserPrincipal.of(MemberTestUtils.createMemberDtoWithId())))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @DisplayName("가게의 id(PK)가 주어지고, 특정 가게에 대한 리뷰 목록을 조회하면, 조회된 리뷰 목록(Slice)을 반환한다.")
    @Test
    void givenPlaceId_whenSearchReviewsOfCertainPlace_thenReturnReviews() throws Exception {
        // given
        long placeId = 1L;
        given(reviewService.searchDtosByPlaceId(eq(placeId), any(Pageable.class)))
                .willReturn(new SliceImpl<>(List.of(ReviewTestUtils.createReviewDtoWithId())));

        // when & then
        mvc.perform(
                        get("/api/reviews?placeId=" + placeId)
                                .with(csrf())
                                .with(user(UserPrincipal.of(MemberTestUtils.createMemberDtoWithId())))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasContent").value(true));
    }
}