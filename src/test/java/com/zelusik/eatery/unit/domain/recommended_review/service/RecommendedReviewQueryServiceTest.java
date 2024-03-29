package com.zelusik.eatery.unit.domain.recommended_review.service;

import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.dto.PlaceWithMarkedStatusDto;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.recommended_review.dto.RecommendedReviewWithPlaceDto;
import com.zelusik.eatery.domain.recommended_review.repository.RecommendedReviewRepository;
import com.zelusik.eatery.domain.recommended_review.service.RecommendedReviewQueryService;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review.dto.ReviewWithPlaceMarkedStatusDto;
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
        List<RecommendedReviewWithPlaceDto> expectedResults = List.of(createRecommendedReviewDto(recommendedReviewId, memberId, createReviewWithPlaceMarkedStatusDto(reviewId, createMemberDto(memberId), createPlaceWithMarkedStatusDto(placeId)), ranking));
        given(recommendedReviewRepository.findAllDtosWithPlaceMarkedStatusByMemberId(memberId)).willReturn(expectedResults);

        // when
        List<RecommendedReviewWithPlaceDto> actualResults = sut.findAllDtosWithPlaceMarkedStatus(memberId);

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

    private ReviewWithPlaceMarkedStatusDto createReviewWithPlaceMarkedStatusDto(long reviewId, MemberDto writer, PlaceWithMarkedStatusDto place) {
        return new ReviewWithPlaceMarkedStatusDto(
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

    private RecommendedReviewWithPlaceDto createRecommendedReviewDto(long id, long memberId, ReviewWithPlaceMarkedStatusDto review, short ranking) {
        return new RecommendedReviewWithPlaceDto(
                id,
                memberId,
                review,
                ranking
        );
    }
}