package com.zelusik.eatery.integration.repository.recommended_review;

import com.zelusik.eatery.config.JpaConfig;
import com.zelusik.eatery.config.QuerydslConfig;
import com.zelusik.eatery.constant.member.LoginType;
import com.zelusik.eatery.constant.member.RoleType;
import com.zelusik.eatery.constant.place.KakaoCategoryGroupCode;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.Bookmark;
import com.zelusik.eatery.domain.RecommendedReview;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.place.Address;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.place.PlaceCategory;
import com.zelusik.eatery.domain.place.Point;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.domain.review.ReviewKeyword;
import com.zelusik.eatery.dto.recommended_review.RecommendedReviewDto;
import com.zelusik.eatery.repository.bookmark.BookmarkRepository;
import com.zelusik.eatery.repository.member.MemberRepository;
import com.zelusik.eatery.repository.place.PlaceRepository;
import com.zelusik.eatery.repository.recommended_review.RecommendedReviewRepository;
import com.zelusik.eatery.repository.review.ReviewKeywordRepository;
import com.zelusik.eatery.repository.review.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Integration] Recommended Review Repository")
@ActiveProfiles("test")
@Import({QuerydslConfig.class, JpaConfig.class})
@DataJpaTest
class RecommendedReviewRepositoryTest {

    private final RecommendedReviewRepository sut;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewKeywordRepository reviewKeywordRepository;
    private final BookmarkRepository bookmarkRepository;

    @Autowired
    public RecommendedReviewRepositoryTest(RecommendedReviewRepository sut, MemberRepository memberRepository, PlaceRepository placeRepository, ReviewRepository reviewRepository, ReviewKeywordRepository reviewKeywordRepository, BookmarkRepository bookmarkRepository) {
        this.sut = sut;
        this.memberRepository = memberRepository;
        this.placeRepository = placeRepository;
        this.reviewRepository = reviewRepository;
        this.reviewKeywordRepository = reviewKeywordRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    @DisplayName("id로 특정 회원이 설정한 추천 리뷰들을 장소 저장 여부와 함께 조회한다.")
    @Test
    void given_whenFindingAllRecommendedReviewDtosWithPlaceMarkedStatusByMemberId_thenReturnRecommendedReviewDtos() {
        // given
        Member member = memberRepository.save(createNewMember("social id", Set.of(RoleType.USER), "member"));
        Place place = placeRepository.save(createNewPlace("kakao place id", "place name"));
        Bookmark bookmark = bookmarkRepository.save(createNewBookmark(member, place));
        Review review1 = reviewRepository.save(createNewReview(member, place));
        Review review2 = reviewRepository.save(createNewReview(member, place));
        Review review3 = reviewRepository.save(createNewReview(member, place));
        review1.getKeywords().add(reviewKeywordRepository.save(createNewReviewKeyword(review1, ReviewKeywordValue.FRESH)));
        review2.getKeywords().add(reviewKeywordRepository.save(createNewReviewKeyword(review2, ReviewKeywordValue.FRESH)));
        review3.getKeywords().add(reviewKeywordRepository.save(createNewReviewKeyword(review3, ReviewKeywordValue.NOISY)));
        List<RecommendedReview> savedRecommendedReviews = sut.saveAll(List.of(
                createNewRecommendedReview(member, review1, (short) 1),
                createNewRecommendedReview(member, review2, (short) 2),
                createNewRecommendedReview(member, review3, (short) 3)
        ));

        // when
        List<RecommendedReviewDto> result = sut.findAllDtosWithPlaceMarkedStatusByMemberId(member.getId());

        // then
        assertThat(result).hasSize(savedRecommendedReviews.size());
        for (int i = 0; i < savedRecommendedReviews.size(); i++) {
            RecommendedReview expectedResult = savedRecommendedReviews.get(i);
            RecommendedReviewDto actualResult = result.get(i);
            assertThat(actualResult)
                    .hasFieldOrPropertyWithValue("id", expectedResult.getId())
                    .hasFieldOrPropertyWithValue("memberId", expectedResult.getMember().getId())
                    .hasFieldOrPropertyWithValue("review.id", expectedResult.getReview().getId())
                    .hasFieldOrPropertyWithValue("review.place.isMarked", true)
                    .hasFieldOrPropertyWithValue("ranking", expectedResult.getRanking());
            assertThat(actualResult.getReview().getKeywords()).hasSize(expectedResult.getReview().getKeywords().size());
            assertThat(actualResult.getReview().getReviewImageDtos()).hasSize(expectedResult.getReview().getReviewImages().size());
        }
    }

    @DisplayName("존재하지 않는 회원 id로 추천 리뷰들을 조회하면, 빈 리스트를 반환한다.")
    @Test
    void given_whenFindingAllRecommendedReviewDtosWithPlaceMarkedStatusByNotExistentMemberId_thenReturnEmptyList() {
        // given

        // when
        List<RecommendedReviewDto> result = sut.findAllDtosWithPlaceMarkedStatusByMemberId(1L);

        // then
        assertThat(result).hasSize(0);
    }

    private Member createNewMember(String socialId, Set<RoleType> roleTypes, String nickname) {
        return Member.of(
                "https://default-profile-image",
                "https://defualt-profile-thumbnail-image",
                socialId,
                LoginType.KAKAO,
                roleTypes,
                "test@test.com" + socialId,
                nickname,
                null,
                null
        );
    }

    private Place createNewPlace(String kakaoPid, String name) {
        return Place.of(
                kakaoPid,
                name,
                "place page url",
                KakaoCategoryGroupCode.FD6,
                new PlaceCategory("한식", "냉면", null),
                null,
                Address.of("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"),
                "homepage url",
                new Point("12.34", "23.45"),
                null
        );
    }

    private Review createNewReview(Member writer, Place place) {
        return Review.of(
                writer,
                place,
                "auto created content",
                "content"
        );
    }

    private Bookmark createNewBookmark(Member member, Place place) {
        return Bookmark.of(member, place);
    }

    private ReviewKeyword createNewReviewKeyword(Review review, ReviewKeywordValue reviewKeywordValue) {
        return ReviewKeyword.of(review, reviewKeywordValue);
    }

    private RecommendedReview createNewRecommendedReview(Member member, Review review, short ranking) {
        return RecommendedReview.of(member, review, ranking);
    }
}