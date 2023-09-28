package com.zelusik.eatery.unit.domain.recommended_review.service;

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
import com.zelusik.eatery.domain.recommended_review.dto.RecommendedReviewDto;
import com.zelusik.eatery.domain.recommended_review.dto.request.BatchUpdateRecommendedReviewsRequest;
import com.zelusik.eatery.domain.recommended_review.entity.RecommendedReview;
import com.zelusik.eatery.domain.recommended_review.repository.RecommendedReviewRepository;
import com.zelusik.eatery.domain.recommended_review.service.RecommendedReviewCommandService;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review.entity.Review;
import com.zelusik.eatery.domain.review.service.ReviewQueryService;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("[Unit] Service(Command) - Recommended review")
@ExtendWith(MockitoExtension.class)
class RecommendedReviewCommandServiceTest {

    @InjectMocks
    private RecommendedReviewCommandService sut;

    @Mock
    private MemberQueryService memberQueryService;
    @Mock
    private ReviewQueryService reviewQueryService;
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
        Review review = createReview(reviewId, member, createPlace(3L, "12345"));
        RecommendedReview expectedResult = createRecommendedReview(4L, member, review, ranking);
        given(memberQueryService.findById(memberId)).willReturn(member);
        given(reviewQueryService.findById(reviewId)).willReturn(review);
        given(recommendedReviewRepository.save(any(RecommendedReview.class))).willReturn(expectedResult);

        // when
        RecommendedReviewDto actualResult = sut.saveRecommendedReview(memberId, reviewId, ranking);

        // then
        then(memberQueryService).should().findById(memberId);
        then(reviewQueryService).should().findById(reviewId);
        then(recommendedReviewRepository).should().save(any(RecommendedReview.class));
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult)
                .hasFieldOrPropertyWithValue("memberId", member.getId())
                .hasFieldOrPropertyWithValue("review.id", review.getId());
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
        Review review1 = createReview(3L, member, createPlace(10L, "123"));
        Review review2 = createReview(4L, member, createPlace(11L, "234"));
        Review review3 = createReview(5L, member, createPlace(12L, "345"));
        List<RecommendedReview> expectedResults = List.of(
                createRecommendedReview(6L, member, review1, (short) 1),
                createRecommendedReview(7L, member, review2, (short) 2),
                createRecommendedReview(8L, member, review3, (short) 3)
        );
        given(memberQueryService.findById(memberId)).willReturn(member);
        willDoNothing().given(recommendedReviewRepository).deleteAllByMember(member);
        willDoNothing().given(recommendedReviewRepository).flush();
        given(reviewQueryService.findById(any(Long.class))).willReturn(review1, review2, review3);
        given(recommendedReviewRepository.saveAll(anyList())).willReturn(expectedResults);

        // when
        List<RecommendedReviewDto> actualResults = sut.batchUpdateRecommendedReviews(memberId, batchUpdateRecommendedReviewsRequest);

        // then
        then(memberQueryService).should().findById(memberId);
        then(recommendedReviewRepository).should().deleteAllByMember(member);
        then(recommendedReviewRepository).should().flush();
        verify(reviewQueryService, times(3)).findById(any(Long.class));
        then(recommendedReviewRepository).should().saveAll(anyList());
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResults).isNotEmpty();
        assertThat(actualResults).hasSize(3);
        for (int i = 0; i < expectedResults.size(); i++) {
            RecommendedReview expectedResult = expectedResults.get(i);
            RecommendedReviewDto actualResult = actualResults.get(i);
            assertThat(actualResult)
                    .hasFieldOrPropertyWithValue("memberId", expectedResult.getMember().getId())
                    .hasFieldOrPropertyWithValue("review.id", expectedResult.getReview().getId())
                    .hasFieldOrPropertyWithValue("ranking", expectedResult.getRanking());
        }
    }

    private void verifyEveryMocksShouldHaveNoMoreInteractions() {
        then(memberQueryService).shouldHaveNoMoreInteractions();
        then(reviewQueryService).shouldHaveNoMoreInteractions();
        then(recommendedReviewRepository).shouldHaveNoMoreInteractions();
    }

    private Member createMember(Long memberId) {
        return createMember(memberId, Set.of(RoleType.USER));
    }

    private Member createMember(Long memberId, Set<RoleType> roleTypes) {
        return Member.of(
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

    private Review createReview(Long reviewId, Member member, Place place) {
        return Review.of(
                reviewId,
                member,
                place,
                "자동 생성된 내용",
                "제출한 내용",
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private RecommendedReview createRecommendedReview(Long id, Member member, Review review, short ranking) {
        return RecommendedReview.of(
                id,
                member,
                review,
                ranking,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}