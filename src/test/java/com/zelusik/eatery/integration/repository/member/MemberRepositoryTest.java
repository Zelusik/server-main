package com.zelusik.eatery.integration.repository.member;

import com.zelusik.eatery.config.JpaConfig;
import com.zelusik.eatery.config.QuerydslConfig;
import com.zelusik.eatery.constant.FoodCategoryValue;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.place.Address;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.place.PlaceCategory;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.domain.review.ReviewKeyword;
import com.zelusik.eatery.dto.member.MemberProfileInfoDto;
import com.zelusik.eatery.exception.member.MemberIdNotFoundException;
import com.zelusik.eatery.repository.member.MemberRepository;
import com.zelusik.eatery.repository.place.PlaceRepository;
import com.zelusik.eatery.repository.review.ReviewKeywordRepository;
import com.zelusik.eatery.repository.review.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static com.zelusik.eatery.util.MemberTestUtils.createNotSavedMember;
import static com.zelusik.eatery.util.PlaceTestUtils.createNewPlace;
import static com.zelusik.eatery.util.ReviewTestUtils.createNewReview;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@DisplayName("[Integration] Member Repository")
@ActiveProfiles("test")
@Import({QuerydslConfig.class, JpaConfig.class})
@DataJpaTest
class MemberRepositoryTest {

    private final MemberRepository sut;
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewKeywordRepository reviewKeywordRepository;

    @Autowired
    public MemberRepositoryTest(MemberRepository sut, PlaceRepository placeRepository, ReviewRepository reviewRepository, ReviewKeywordRepository reviewKeywordRepository) {
        this.sut = sut;
        this.placeRepository = placeRepository;
        this.reviewRepository = reviewRepository;
        this.reviewKeywordRepository = reviewKeywordRepository;
    }

    @DisplayName("주어진 검색 키워드로 회원을 검색한다.")
    @Test
    void givenSearchKeyword_whenSearchMembersByKeyword_thenReturnSearchedMembers() {
        // given
        Member member1 = sut.save(createNotSavedMember("1", "7 옥타브 고양이"));
        Member member2 = sut.save(createNotSavedMember("2", "하얀 강아지"));
        Member member3 = sut.save(createNotSavedMember("3", "까만 고양이"));
        Member member4 = sut.save(createNotSavedMember("4", "개냥이"));
        Member member5 = sut.save(createNotSavedMember("5", "반려묘"));
        String searchKeyword = "고양이";

        // when
        Slice<Member> result = sut.searchByKeyword(searchKeyword, Pageable.ofSize(30));

        // then
        assertThat(result.getNumberOfElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getId()).isEqualTo(member1.getId());
        assertThat(result.getContent().get(1).getId()).isEqualTo(member3.getId());
    }

    @DisplayName("id로 회원 프로필 정보를 조회한다.")
    @Test
    void given_whenGetMemberProfileInfoWithMemberId_thenReturnMemberProfileInfos() {
        // given
        Member member = sut.save(createNotSavedMember("1", "돼지 고양이"));
        Place place1 = placeRepository.save(createNewPlace("123", "place1", new PlaceCategory("한식", "냉면", null), new Address("sido", "sigungu", "연남동 123", "연남로 123")));
        Place place2 = placeRepository.save(createNewPlace("234", "place2", new PlaceCategory("일식", "라멘", null), new Address("sido", "sigungu", "이의동 123", "이의로 123")));
        Review review1 = reviewRepository.save(createNewReview(member, place2, List.of()));
        Review review2 = reviewRepository.save(createNewReview(member, place2, List.of()));
        Review review3 = reviewRepository.save(createNewReview(member, place1, List.of()));
        review1.getKeywords().add(reviewKeywordRepository.save(createNewReviewKeyword(review1, ReviewKeywordValue.FRESH)));
        review2.getKeywords().add(reviewKeywordRepository.save(createNewReviewKeyword(review2, ReviewKeywordValue.FRESH)));
        review3.getKeywords().add(reviewKeywordRepository.save(createNewReviewKeyword(review3, ReviewKeywordValue.NOISY)));

        // when
        MemberProfileInfoDto result = sut.getMemberProfileInfoById(member.getId());

        // then
        assertThat(result)
                .hasFieldOrPropertyWithValue("id", member.getId())
                .hasFieldOrPropertyWithValue("profileImageUrl", member.getProfileImageUrl())
                .hasFieldOrPropertyWithValue("profileThumbnailImageUrl", member.getProfileThumbnailImageUrl())
                .hasFieldOrPropertyWithValue("nickname", member.getNickname())
                .hasFieldOrPropertyWithValue("gender", member.getGender())
                .hasFieldOrPropertyWithValue("birthDay", member.getBirthDay())
                .hasFieldOrPropertyWithValue("numOfReviews", 3)
                .hasFieldOrPropertyWithValue("influence", 0)
                .hasFieldOrPropertyWithValue("numOfFollowers", 0)
                .hasFieldOrPropertyWithValue("numOfFollowings", 0)
                .hasFieldOrPropertyWithValue("mostVisitedLocation", "이의동")
                .hasFieldOrPropertyWithValue("mostTaggedReviewKeyword", ReviewKeywordValue.FRESH)
                .hasFieldOrPropertyWithValue("mostEatenFoodCategory", FoodCategoryValue.JAPANESE);
    }

    @DisplayName("존재하지 않는 id로 회원 프로필 정보를 조회하면, 예외가 발생한다.")
    @Test
    void given_whenGetMemberProfileInfoWithNotExistentMemberId_thenReturnMemberProfileInfos() {
        // given
        Member member = sut.save(createNotSavedMember("1", "돼지 고양이"));

        // when
        Throwable t = catchThrowable(() -> sut.getMemberProfileInfoById(100));

        // then
        assertThat(t).isInstanceOf(MemberIdNotFoundException.class);
    }

    private ReviewKeyword createNewReviewKeyword(Review review, ReviewKeywordValue reviewKeywordValue) {
        return createReviewKeyword(null, review, reviewKeywordValue);
    }

    private ReviewKeyword createReviewKeyword(Long reviewKeywordId, Review review, ReviewKeywordValue reviewKeywordValue) {
        return ReviewKeyword.of(
                reviewKeywordId,
                review,
                reviewKeywordValue,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}