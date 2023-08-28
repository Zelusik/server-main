package com.zelusik.eatery.unit.service;

import com.zelusik.eatery.domain.RecommendedReview;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.dto.recommended_review.RecommendedReviewDto;
import com.zelusik.eatery.repository.recommended_review.RecommendedReviewRepository;
import com.zelusik.eatery.service.MemberService;
import com.zelusik.eatery.service.RecommendedReviewService;
import com.zelusik.eatery.service.ReviewService;
import com.zelusik.eatery.util.MemberTestUtils;
import com.zelusik.eatery.util.PlaceTestUtils;
import com.zelusik.eatery.util.RecommendedReviewTestUtils;
import com.zelusik.eatery.util.ReviewTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.zelusik.eatery.util.MemberTestUtils.createMember;
import static com.zelusik.eatery.util.PlaceTestUtils.createPlace;
import static com.zelusik.eatery.util.RecommendedReviewTestUtils.createRecommendedReview;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Recommended Review Service")
@ExtendWith(MockitoExtension.class)
class RecommendedReviewServiceTest {

    @InjectMocks
    private RecommendedReviewService sut;

    @Mock
    private MemberService memberService;
    @Mock
    private ReviewService reviewService;
    @Mock
    private RecommendedReviewRepository recommendedReviewRepository;

    @DisplayName("리뷰 id와 순위가 주어지고, 주어진 리뷰를 추천 리뷰로 등록한다.")
    @Test
    void givenReviewIdAndRanking_whenSavingRecommendedReview_thenSavesRecommendedReview() {
        // given
        long memberId = 1L;
        long reviewId = 2L;
        short ranking = 1;
        Member member = createMember(memberId);
        Review review = ReviewTestUtils.createReview(reviewId, member, createPlace(3L, "12345"));
        RecommendedReview expectedResult = createRecommendedReview(4L, member, review, ranking);
        given(memberService.findById(memberId)).willReturn(member);
        given(reviewService.findById(reviewId)).willReturn(review);
        given(recommendedReviewRepository.save(any(RecommendedReview.class))).willReturn(expectedResult);

        // when
        RecommendedReviewDto actualResult = sut.saveRecommendedReview(memberId, reviewId, ranking);

        // then
        then(memberService).should().findById(memberId);
        then(reviewService).should().findById(reviewId);
        then(recommendedReviewRepository).should().save(any(RecommendedReview.class));
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult)
                .hasFieldOrPropertyWithValue("memberId", member.getId())
                .hasFieldOrPropertyWithValue("reviewId", review.getId());
    }

    private void verifyEveryMocksShouldHaveNoMoreInteractions() {
        then(memberService).shouldHaveNoMoreInteractions();
        then(reviewService).shouldHaveNoMoreInteractions();
        then(recommendedReviewRepository).shouldHaveNoMoreInteractions();
    }
}