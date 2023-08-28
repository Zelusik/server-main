package com.zelusik.eatery.unit.service;

import com.zelusik.eatery.domain.RecommendedReview;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.dto.recommended_review.RecommendedReviewDto;
import com.zelusik.eatery.dto.recommended_review.request.BatchUpdateRecommendedReviewsRequest;
import com.zelusik.eatery.repository.recommended_review.RecommendedReviewRepository;
import com.zelusik.eatery.service.MemberService;
import com.zelusik.eatery.service.RecommendedReviewService;
import com.zelusik.eatery.service.ReviewService;
import com.zelusik.eatery.util.ReviewTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.zelusik.eatery.util.MemberTestUtils.createMember;
import static com.zelusik.eatery.util.PlaceTestUtils.createPlace;
import static com.zelusik.eatery.util.RecommendedReviewTestUtils.createRecommendedReview;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    @DisplayName("새로 갱신하고자 하는 추천 리뷰 세 개의 정보가 주어지고, 추천 리뷰를 batch update하면, 추천 리뷰가 전달받은 리뷰로 갱신된다.")
    @Test
    void givenNewRecommendedReviewInfos_whenBatchUpdateRecommendedReviews_thenUpdateRecommendedReviews() {
        // given
        long memberId = 1L;
        BatchUpdateRecommendedReviewsRequest batchUpdateRecommendedReviewsRequest = new BatchUpdateRecommendedReviewsRequest(List.of(
                new BatchUpdateRecommendedReviewsRequest.RecommendedReviewRequest(3L, (short) 1),
                new BatchUpdateRecommendedReviewsRequest.RecommendedReviewRequest(4L, (short) 2),
                new BatchUpdateRecommendedReviewsRequest.RecommendedReviewRequest(5L, (short) 3)
        ));
        Member member = createMember(memberId);
        Review review1 = ReviewTestUtils.createReview(3L, member, createPlace(10L, "123"));
        Review review2 = ReviewTestUtils.createReview(4L, member, createPlace(11L, "234"));
        Review review3 = ReviewTestUtils.createReview(5L, member, createPlace(12L, "345"));
        List<RecommendedReview> expectedResults = List.of(
                createRecommendedReview(6L, member, review1, (short) 1),
                createRecommendedReview(7L, member, review2, (short) 2),
                createRecommendedReview(8L, member, review3, (short) 3)
        );
        given(memberService.findById(memberId)).willReturn(member);
        willDoNothing().given(recommendedReviewRepository).deleteAllByMember(member);
        given(reviewService.findById(any(Long.class))).willReturn(review1, review2, review3);
        given(recommendedReviewRepository.saveAll(anyList())).willReturn(expectedResults);

        // when
        List<RecommendedReviewDto> actualResults = sut.batchUpdateRecommendedReviews(memberId, batchUpdateRecommendedReviewsRequest);

        // then
        then(memberService).should().findById(memberId);
        then(recommendedReviewRepository).should().deleteAllByMember(member);
        verify(reviewService, times(3)).findById(any(Long.class));
        then(recommendedReviewRepository).should().saveAll(anyList());
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResults).isNotEmpty();
        assertThat(actualResults).hasSize(3);
        for (int i = 0; i < expectedResults.size(); i++) {
            RecommendedReview expectedResult = expectedResults.get(i);
            RecommendedReviewDto actualResult = actualResults.get(i);
            assertThat(actualResult)
                    .hasFieldOrPropertyWithValue("memberId", expectedResult.getMember().getId())
                    .hasFieldOrPropertyWithValue("reviewId", expectedResult.getReview().getId())
                    .hasFieldOrPropertyWithValue("ranking", expectedResult.getRanking());
        }
    }

    private void verifyEveryMocksShouldHaveNoMoreInteractions() {
        then(memberService).shouldHaveNoMoreInteractions();
        then(reviewService).shouldHaveNoMoreInteractions();
        then(recommendedReviewRepository).shouldHaveNoMoreInteractions();
    }
}