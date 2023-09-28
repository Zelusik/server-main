package com.zelusik.eatery.unit.domain.recommended_review.service;

import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.recommended_review.dto.RecommendedReviewDto;
import com.zelusik.eatery.domain.recommended_review.repository.RecommendedReviewRepository;
import com.zelusik.eatery.domain.recommended_review.service.RecommendedReviewQueryService;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review.dto.ReviewDto;
import com.zelusik.eatery.domain.review_image.dto.ReviewImageDto;
import com.zelusik.eatery.global.common.constant.EateryConstants;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Service(Query) - Recommended review")
@ExtendWith(MockitoExtension.class)
class RecommendedReviewQueryServiceTest {

    @InjectMocks
    private RecommendedReviewQueryService sut;

    @Mock
    private RecommendedReviewRepository recommendedReviewRepository;

    @DisplayName("회원 id가 주어지고, id에 해당하는 회원이 설정한 추천 리뷰들을 함께 조회한다.")
    @Test
    void given_whenFindingRecommendedReviewsWithMemberId_thenReturnRecommendedReviews() {
        // given
        long memberId = 1L;
        long placeId = 2L;
        long recommendedReviewId = 3L;
        long reviewId = 4L;
        short ranking = 3;
        List<RecommendedReviewDto> expectedResults = List.of(createRecommendedReviewDto(recommendedReviewId, memberId, createReviewDto(reviewId, createMemberDto(memberId), createPlaceDto(placeId)), ranking));
        given(recommendedReviewRepository.findAllDtosWithPlaceMarkedStatusByMemberId(memberId)).willReturn(expectedResults);

        // when
        List<RecommendedReviewDto> actualResults = sut.findAllDtosWithPlaceMarkedStatus(memberId);

        // then
        then(recommendedReviewRepository).should().findAllDtosWithPlaceMarkedStatusByMemberId(memberId);
        then(recommendedReviewRepository).shouldHaveNoMoreInteractions();
        assertThat(actualResults).hasSize(expectedResults.size());
        assertThat(actualResults.get(0))
                .hasFieldOrPropertyWithValue("id", expectedResults.get(0).getId())
                .hasFieldOrPropertyWithValue("memberId", expectedResults.get(0).getMemberId())
                .hasFieldOrPropertyWithValue("review.id", expectedResults.get(0).getReview().getId())
                .hasFieldOrPropertyWithValue("ranking", expectedResults.get(0).getRanking());
    }

    private MemberDto createMemberDto(Long memberId) {
        return createMemberDto(memberId, Set.of(RoleType.USER));
    }

    private MemberDto createMemberDto(Long memberId, Set<RoleType> roleTypes) {
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

    private PlaceDto createPlaceDto(Long placeId) {
        return new PlaceDto(
                placeId,
                List.of(ReviewKeywordValue.FRESH),
                "308342289",
                "연남토마 본점",
                "http://place.map.kakao.com/308342289",
                KakaoCategoryGroupCode.FD6,
                PlaceCategory.of("음식점 > 퓨전요리 > 퓨전일식"),
                "02-332-8064",
                Address.of("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"),
                "http://place.map.kakao.com/308342289",
                new Point("37.5595073462493", "126.921462488105"),
                null,
                List.of(),
                null,
                false
        );
    }

    private ReviewDto createReviewDto(long reviewId, MemberDto writer, PlaceDto place) {
        return new ReviewDto(
                reviewId,
                writer,
                place,
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
                        "thumbnailUrl")),
                LocalDateTime.now()
        );
    }

    private RecommendedReviewDto createRecommendedReviewDto(long id, long memberId, ReviewDto review, short ranking) {
        return new RecommendedReviewDto(
                id,
                memberId,
                review,
                ranking
        );
    }
}