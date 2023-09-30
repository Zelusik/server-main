package com.zelusik.eatery.integration.domain.review.repository;

import com.zelusik.eatery.global.config.JpaConfig;
import com.zelusik.eatery.global.config.QuerydslConfig;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.bookmark.entity.Bookmark;
import com.zelusik.eatery.domain.favorite_food_category.entity.FavoriteFoodCategory;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.review.entity.Review;
import com.zelusik.eatery.domain.review.dto.ReviewWithPlaceMarkedStatusDto;
import com.zelusik.eatery.domain.bookmark.repository.BookmarkRepository;
import com.zelusik.eatery.domain.favorite_food_category.repository.FavoriteFoodCategoryRepository;
import com.zelusik.eatery.domain.member.repository.MemberRepository;
import com.zelusik.eatery.domain.place.repository.PlaceRepository;
import com.zelusik.eatery.domain.review.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static com.zelusik.eatery.domain.review.constant.ReviewEmbedOption.PLACE;
import static com.zelusik.eatery.domain.review.constant.ReviewEmbedOption.WRITER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("[Integration] Repository - Review")
@ActiveProfiles("test")
@Import({QuerydslConfig.class, JpaConfig.class})
@DataJpaTest
class ReviewRepositoryTest {

    private final ReviewRepository sut;
    private final MemberRepository memberRepository;
    private final FavoriteFoodCategoryRepository favoriteFoodCategoryRepository;
    private final PlaceRepository placeRepository;
    private final BookmarkRepository bookmarkRepository;

    @Autowired
    public ReviewRepositoryTest(ReviewRepository sut, MemberRepository memberRepository, FavoriteFoodCategoryRepository favoriteFoodCategoryRepository, PlaceRepository placeRepository, BookmarkRepository bookmarkRepository) {
        this.sut = sut;
        this.memberRepository = memberRepository;
        this.favoriteFoodCategoryRepository = favoriteFoodCategoryRepository;
        this.placeRepository = placeRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    @DisplayName("리뷰들을 조회한다.")
    @Test
    void given_whenFindReviewDtosWithWriterAndPlace_thenReturnResult() {
        // given
        Member member = memberRepository.save(createNewMember("1"));
        Place place = placeRepository.save(createNewPlace("1"));
        sut.saveAll(List.of(
                createNewReview(member, place),
                createNewReview(member, place),
                createNewReview(member, place)
        ));
        bookmarkRepository.save(createNewBookmark(member, place));

        // when
        Slice<ReviewWithPlaceMarkedStatusDto> results = sut.findDtos(member.getId(), null, null, List.of(WRITER, PLACE), Pageable.ofSize(10));

        // then
        assertThat(results.getNumberOfElements()).isEqualTo(3);
        assertThat(results.getContent()).hasSize(3);
        for (ReviewWithPlaceMarkedStatusDto result : results) {
            assertThat(result)
                    .hasFieldOrProperty("writer").isNotNull()
                    .hasFieldOrProperty("place").isNotNull()
                    .hasFieldOrPropertyWithValue("place.isMarked", true)
                    .hasFieldOrProperty("keywords").isNotNull()
                    .hasFieldOrProperty("autoCreatedContent").isNotNull()
                    .hasFieldOrProperty("content").isNotNull()
                    .hasFieldOrProperty("reviewImageDtos").isNotNull();
        }
    }

    @DisplayName("Writer id가 주어지고, 주어진 id와 일치하는 리뷰 작성자 있는 리뷰들을 조회한다.")
    @Test
    void givenWriterId_whenFindReviewDtosWithWriterAndPlace_thenReturnResult() {
        // given
        Member member1 = memberRepository.save(createNewMember("1"));
        Member member2 = memberRepository.save(createNewMember("2"));
        Place place = placeRepository.save(createNewPlace("1"));
        sut.saveAll(List.of(
                createNewReview(member1, place),
                createNewReview(member1, place),
                createNewReview(member2, place)
        ));

        // when
        Slice<ReviewWithPlaceMarkedStatusDto> results = sut.findDtos(member1.getId(), member1.getId(), null, List.of(WRITER, PLACE), Pageable.ofSize(10));

        // then
        assertThat(results.getNumberOfElements()).isEqualTo(2);
        assertThat(results.getContent()).hasSize(2);
        for (ReviewWithPlaceMarkedStatusDto result : results) {
            assertThat(result)
                    .hasFieldOrProperty("writer").isNotNull()
                    .hasFieldOrPropertyWithValue("writer.socialUid", member1.getSocialUid())
                    .hasFieldOrProperty("place").isNotNull()
                    .hasFieldOrPropertyWithValue("place.kakaoPid", place.getKakaoPid())
                    .hasFieldOrProperty("keywords").isNotNull()
                    .hasFieldOrProperty("autoCreatedContent").isNotNull()
                    .hasFieldOrProperty("content").isNotNull()
                    .hasFieldOrProperty("reviewImageDtos").isNotNull();
        }
    }

    @DisplayName("Place id가 주어지고, 주어진 id와 일치하는 가게에 작성된 리뷰들을 조회한다.")
    @Test
    void givenPlaceId_whenFindReviewDtosWithWriterAndPlace_thenReturnResult() {
        // given
        Member member = memberRepository.save(createNewMember("1"));
        Place place1 = placeRepository.save(createNewPlace("1"));
        Place place2 = placeRepository.save(createNewPlace("2"));
        sut.saveAll(List.of(
                createNewReview(member, place1),
                createNewReview(member, place2),
                createNewReview(member, place2)
        ));

        // when
        Slice<ReviewWithPlaceMarkedStatusDto> results = sut.findDtos(member.getId(), null, place1.getId(), List.of(WRITER, PLACE), Pageable.ofSize(10));

        // then
        assertThat(results.getNumberOfElements()).isEqualTo(1);
        assertThat(results.getContent()).hasSize(1);
        for (ReviewWithPlaceMarkedStatusDto result : results) {
            assertThat(result)
                    .hasFieldOrProperty("writer").isNotNull()
                    .hasFieldOrPropertyWithValue("writer.socialUid", member.getSocialUid())
                    .hasFieldOrProperty("place").isNotNull()
                    .hasFieldOrPropertyWithValue("place.kakaoPid", place1.getKakaoPid())
                    .hasFieldOrProperty("keywords").isNotNull()
                    .hasFieldOrProperty("autoCreatedContent").isNotNull()
                    .hasFieldOrProperty("content").isNotNull()
                    .hasFieldOrProperty("reviewImageDtos").isNotNull();
        }
    }

    @DisplayName("리뷰 작성자 id와 place id가 주어지고, 주어진 id와 일치하는 리뷰 작성자와 가게가 있는 리뷰들을 조회한다.")
    @Test
    void givenWriterIdAndPlaceId_whenFindReviewDtosWithWriterAndPlace_thenReturnResult() {
        // given
        Member member1 = memberRepository.save(createNewMember("1"));
        Member member2 = memberRepository.save(createNewMember("2"));
        Place place1 = placeRepository.save(createNewPlace("1"));
        Place place2 = placeRepository.save(createNewPlace("2"));
        sut.saveAll(List.of(
                createNewReview(member1, place1),
                createNewReview(member1, place2),
                createNewReview(member1, place2),
                createNewReview(member2, place2),
                createNewReview(member2, place2)
        ));

        // when
        Slice<ReviewWithPlaceMarkedStatusDto> results = sut.findDtos(member1.getId(), member1.getId(), place2.getId(), List.of(WRITER, PLACE), Pageable.ofSize(10));

        // then
        assertThat(results.getNumberOfElements()).isEqualTo(2);
        assertThat(results.getContent()).hasSize(2);
        for (ReviewWithPlaceMarkedStatusDto result : results) {
            assertThat(result)
                    .hasFieldOrProperty("writer").isNotNull()
                    .hasFieldOrPropertyWithValue("writer.socialUid", member1.getSocialUid())
                    .hasFieldOrProperty("place").isNotNull()
                    .hasFieldOrPropertyWithValue("place.kakaoPid", place2.getKakaoPid())
                    .hasFieldOrProperty("keywords").isNotNull()
                    .hasFieldOrProperty("autoCreatedContent").isNotNull()
                    .hasFieldOrProperty("content").isNotNull()
                    .hasFieldOrProperty("reviewImageDtos").isNotNull();
        }
    }

    @DisplayName("작성자와 장소 정보 모두 제외한 오직 리뷰만을 조회한다.")
    @Test
    void given_whenFindReviewDtos_thenReturnResult() {
        // given
        Member member = memberRepository.save(createNewMember("1"));
        Place place = placeRepository.save(createNewPlace("1"));
        sut.saveAll(List.of(
                createNewReview(member, place),
                createNewReview(member, place),
                createNewReview(member, place)
        ));

        // when
        Slice<ReviewWithPlaceMarkedStatusDto> results = sut.findDtos(member.getId(), null, null, List.of(), Pageable.ofSize(10));

        // then
        assertThat(results.getNumberOfElements()).isEqualTo(3);
        assertThat(results.getContent()).hasSize(3);
        for (ReviewWithPlaceMarkedStatusDto result : results) {
            assertThat(result)
                    .hasFieldOrPropertyWithValue("writer", null)
                    .hasFieldOrPropertyWithValue("place", null)
                    .hasFieldOrProperty("keywords").isNotNull()
                    .hasFieldOrProperty("autoCreatedContent").isNotNull()
                    .hasFieldOrProperty("content").isNotNull()
                    .hasFieldOrProperty("reviewImageDtos").isNotNull();
        }
    }

    @DisplayName("장소 정보를 제외한 리뷰를 조회한다.")
    @Test
    void given_whenFindReviewDtosWithWriter_thenReturnResult() {
        // given
        Member member = memberRepository.save(createNewMember("1"));
        Place place = placeRepository.save(createNewPlace("1"));
        sut.saveAll(List.of(
                createNewReview(member, place),
                createNewReview(member, place),
                createNewReview(member, place)
        ));

        // when
        Slice<ReviewWithPlaceMarkedStatusDto> results = sut.findDtos(member.getId(), null, null, List.of(WRITER), Pageable.ofSize(10));

        // then
        assertThat(results.getNumberOfElements()).isEqualTo(3);
        assertThat(results.getContent()).hasSize(3);
        for (ReviewWithPlaceMarkedStatusDto result : results) {
            assertThat(result)
                    .hasFieldOrProperty("writer").isNotNull()
                    .hasFieldOrPropertyWithValue("place", null)
                    .hasFieldOrProperty("keywords").isNotNull()
                    .hasFieldOrProperty("autoCreatedContent").isNotNull()
                    .hasFieldOrProperty("content").isNotNull()
                    .hasFieldOrProperty("reviewImageDtos").isNotNull();
        }
    }

    @DisplayName("작성자 정보를 제외한 리뷰를 조회한다.")
    @Test
    void given_whenFindReviewDtosWithPlace_thenReturnResult() {
        // given
        Member member = memberRepository.save(createNewMember("1"));
        Place place = placeRepository.save(createNewPlace("1"));
        sut.saveAll(List.of(
                createNewReview(member, place),
                createNewReview(member, place),
                createNewReview(member, place)
        ));

        // when
        Slice<ReviewWithPlaceMarkedStatusDto> results = sut.findDtos(member.getId(), null, null, List.of(PLACE), Pageable.ofSize(10));

        // then
        assertThat(results.getNumberOfElements()).isEqualTo(3);
        assertThat(results.getContent()).hasSize(3);
        for (ReviewWithPlaceMarkedStatusDto result : results) {
            assertThat(result)
                    .hasFieldOrPropertyWithValue("writer", null)
                    .hasFieldOrProperty("place").isNotNull()
                    .hasFieldOrProperty("keywords").isNotNull()
                    .hasFieldOrProperty("autoCreatedContent").isNotNull()
                    .hasFieldOrProperty("content").isNotNull()
                    .hasFieldOrProperty("reviewImageDtos").isNotNull();
        }
    }

    @DisplayName("리뷰 피드를 조회한다.")
    @Test
    void given_whenFindReviewFeed_thenReturnReview() {
        // given
        Member member1 = memberRepository.save(createNewMember("1"));
        FavoriteFoodCategory favoriteFoodCategoryOfMember1 = favoriteFoodCategoryRepository.save(createNewFavoriteFoodCategory(member1, FoodCategoryValue.KOREAN));
        member1.getFavoriteFoodCategories().add(favoriteFoodCategoryOfMember1);
        Member member2 = memberRepository.save(createNewMember("2"));
        Place place1 = placeRepository.save(createNewPlace("1", new PlaceCategory("한식", null, null)));
        Place place2 = placeRepository.save(createNewPlace("2", new PlaceCategory("일식", null, null)));
        sut.saveAll(List.of(
                createNewReview(member1, place1),
                createNewReview(member1, place2),
                createNewReview(member2, place1),
                createNewReview(member2, place2),
                createNewReview(member2, place1)
        ));

        // when
        Slice<ReviewWithPlaceMarkedStatusDto> results = sut.findReviewFeed(member1.getId(), Pageable.ofSize(10));

        // then
        assertThat(results).hasSize(3);  // 내가 작성한 리뷰는 조회되지 않음
        List<ReviewWithPlaceMarkedStatusDto> content = results.getContent();
        for (int i = 0; i < results.getNumberOfElements(); i++) {
            ReviewWithPlaceMarkedStatusDto result = content.get(i);
            assertThat(result).hasFieldOrPropertyWithValue("writer.id", member2.getId());
            // 리뷰를 작성한 장소의 카테고리가 내가 선호하는 음식 카테고리에 해당되는 리뷰의 우선순위가 더 높다
            if (i < results.getNumberOfElements() - 1
                && result.getPlace().getCategory().getFirstCategory().equals("일식")) {
                assertEquals("일식", content.get(i + 1).getPlace().getCategory().getFirstCategory());
            }
        }
    }

    private Member createNewMember(String socialUid) {
        return createNewMember(socialUid, Set.of(RoleType.USER));
    }

    private Member createNewMember(String socialUid, Set<RoleType> roleTypes) {
        return Member.of(
                "https://default-profile-image",
                "https://defualt-profile-thumbnail-image",
                socialUid,
                LoginType.KAKAO,
                roleTypes,
                "test" + socialUid + "@test.com",
                "test" + socialUid,
                null,
                Gender.ETC
        );
    }

    private FavoriteFoodCategory createNewFavoriteFoodCategory(Member member, FoodCategoryValue foodCategory) {
        return FavoriteFoodCategory.of(member, foodCategory);
    }

    private Place createNewPlace(String kakaoPid) {
        return createNewPlace(kakaoPid, new PlaceCategory("한식", "냉면", null));
    }

    private Place createNewPlace(String kakaoPid, PlaceCategory placeCategory) {
        return Place.of(
                kakaoPid,
                "place name " + kakaoPid,
                "https://place.map.kakao.com/" + kakaoPid,
                KakaoCategoryGroupCode.FD6,
                placeCategory,
                null,
                new Address("sido", "sgg", "lot number address", "road address"),
                null,
                new Point("37", "127"),
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
}
