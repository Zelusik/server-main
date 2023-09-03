package com.zelusik.eatery.integration.repository.place;

import com.zelusik.eatery.config.JpaConfig;
import com.zelusik.eatery.config.QuerydslConfig;
import com.zelusik.eatery.constant.member.LoginType;
import com.zelusik.eatery.constant.member.RoleType;
import com.zelusik.eatery.constant.place.DayOfWeek;
import com.zelusik.eatery.constant.place.FilteringType;
import com.zelusik.eatery.constant.place.KakaoCategoryGroupCode;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.Bookmark;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.place.*;
import com.zelusik.eatery.dto.place.PlaceDto;
import com.zelusik.eatery.dto.place.PlaceFilteringKeywordDto;
import com.zelusik.eatery.dto.place.request.FindNearPlacesFilteringConditionRequest;
import com.zelusik.eatery.repository.bookmark.BookmarkRepository;
import com.zelusik.eatery.repository.member.MemberRepository;
import com.zelusik.eatery.repository.place.OpeningHoursRepository;
import com.zelusik.eatery.repository.place.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static com.zelusik.eatery.constant.place.DayOfWeek.MON;
import static com.zelusik.eatery.constant.place.DayOfWeek.WED;
import static com.zelusik.eatery.constant.review.ReviewKeywordValue.*;
import static com.zelusik.eatery.service.PlaceService.MAX_NUM_OF_PLACE_IMAGES;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 대한민국 북-남 거리 약 1,100km
 */

@DisplayName("[Integration] Place Repository")
@ActiveProfiles("test")
@Import({QuerydslConfig.class, JpaConfig.class})
@DataJpaTest
class PlaceRepositoryTest {

    private final PlaceRepository sut;
    private final MemberRepository memberRepository;
    private final OpeningHoursRepository openingHoursRepository;
    private final BookmarkRepository bookmarkRepository;

    @Autowired
    public PlaceRepositoryTest(PlaceRepository placeRepository, MemberRepository memberRepository, @Autowired OpeningHoursRepository openingHoursRepository, BookmarkRepository bookmarkRepository) {
        this.sut = placeRepository;
        this.memberRepository = memberRepository;
        this.openingHoursRepository = openingHoursRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    @DisplayName("장소들이 존재하고, 중심 좌표 주변의 장소를 조회하면, 거리순으로 정렬된 장소 목록이 반환된다.")
    @Test
    void givenPlaces_whenFindNearBy_thenReturnPlaceSliceSortedByDistance() {
        // given
        Long memberId = 1L;
        Point center = new Point("37", "127");
        FindNearPlacesFilteringConditionRequest filteringCondition = new FindNearPlacesFilteringConditionRequest(null, null, null, false);
        for (int i = 0; i < 50; i++) {
            sut.save(createPlace(
                    (long) (i + 1),
                    String.valueOf(i),
                    null,
                    center.getLat(),
                    String.valueOf(Integer.parseInt(center.getLng()) + Math.random()),
                    null
            ));
        }

        // when
        Slice<PlaceDto> places = sut.findDtosNearBy(memberId, filteringCondition, center, 1100, MAX_NUM_OF_PLACE_IMAGES, PageRequest.of(0, 30));

        // then
        assertThat(places.getSize()).isEqualTo(30);
        assertThat(places.hasNext()).isTrue();
        for (int i = 0; i < places.getSize() - 1; i++) {
            PlaceDto curPlace = places.getContent().get(i);
            PlaceDto nextPlace = places.getContent().get(i + 1);
            assertThat(calculateDiff(center.getLng(), curPlace.getPoint().getLng()))
                    .isLessThanOrEqualTo(calculateDiff(center.getLng(), nextPlace.getPoint().getLng()));
        }
    }

    private double calculateDiff(String centerLng, String placeLng) {
        return Math.abs(Double.parseDouble(centerLng) - Double.parseDouble(placeLng));
    }

    @DisplayName("주변 장소를 조회하면, 50km와 1100km 내에 있는 가게들을 조회한다.")
    @Test
    void givenPlaces_whenFindNearBy_thenReturnPlacesWith50kmAnd1100km() {
        // given
        Long memberId = 1L;
        Point pos = new Point("37.5776087830657", "126.976896737645");  // 경복궁
        Place place1 = sut.save(createPlace(1L, "1", "성심당", "36.32765802936324", "127.42727719121109", null));    // 대전
        place1.getOpeningHoursList().add(openingHoursRepository.save(createOpeningHours(1L, place1, MON)));
        sut.save(createPlace(2L, "2", "해운대암소갈비집", "35.163310169485634", "129.1666092786243", null));  // 부산
        Place place3 = sut.save(createPlace(3L, "3", "연남토마 본점", "37.5595073462493", "126.921462488105", null));   // 서울
        place3.getOpeningHoursList().add(openingHoursRepository.save(createOpeningHours(2L, place3, MON)));
        sut.save(createPlace(4L, "4", "연돈", "33.258895288625645", "126.40715814631936", null));   // 제주
        Place place5 = sut.save(createPlace(5L, "5", "본수원갈비", "37.27796181430103", "127.04060364807957", null));  // 수원
        place5.getOpeningHoursList().add(openingHoursRepository.save(createOpeningHours(3L, place5, WED)));

        // when
        Pageable pageable = Pageable.ofSize(30);
        Slice<PlaceDto> placesLimit50 = sut.findDtosNearBy(memberId, new FindNearPlacesFilteringConditionRequest(null, null, null, false), pos, 50, MAX_NUM_OF_PLACE_IMAGES, pageable);
        Slice<PlaceDto> placesLimit1100 = sut.findDtosNearBy(memberId, new FindNearPlacesFilteringConditionRequest(null, null, null, false), pos, 1100, MAX_NUM_OF_PLACE_IMAGES, pageable);
        Slice<PlaceDto> placesLimit1100DaysMon = sut.findDtosNearBy(memberId, new FindNearPlacesFilteringConditionRequest(null, List.of(MON), null, false), pos, 1100, MAX_NUM_OF_PLACE_IMAGES, pageable);
        Slice<PlaceDto> placesLimit1100DaysWed = sut.findDtosNearBy(memberId, new FindNearPlacesFilteringConditionRequest(null, List.of(WED), null, false), pos, 1100, MAX_NUM_OF_PLACE_IMAGES, pageable);
        Slice<PlaceDto> placesLimit1100DaysMonAndWed = sut.findDtosNearBy(memberId, new FindNearPlacesFilteringConditionRequest(null, List.of(MON, WED), null, false), pos, 1100, MAX_NUM_OF_PLACE_IMAGES, pageable);

        // then
        assertThat(placesLimit50.getNumberOfElements()).isEqualTo(2);
        assertThat(placesLimit1100.getNumberOfElements()).isEqualTo(5);
        assertThat(placesLimit1100DaysMon.getNumberOfElements()).isEqualTo(2);
        assertThat(placesLimit1100DaysWed.getNumberOfElements()).isEqualTo(1);
        assertThat(placesLimit1100DaysMonAndWed.getNumberOfElements()).isEqualTo(3);
    }

    @DisplayName("검색 키워드가 주어지고, 키워드로 장소를 조회한다.")
    @Test
    void givenSearchKeyword_whenSearching_thenReturnPlaces() {
        // given
        Place place1 = sut.save(createNewPlace("1", "김치 맛집"));
        Place place2 = sut.save(createNewPlace("2", "햄버거 맛집"));
        Place place3 = sut.save(createNewPlace("3", "그냥 식당"));
        Place place4 = sut.save(createNewPlace("4", "돈까스 맛집"));
        Place place5 = sut.save(createNewPlace("5", "테스트"));
        Pageable pageable = Pageable.ofSize(30);
        String keyword = "맛집";

        // when
        Slice<Place> result = sut.searchByKeyword(keyword, pageable);

        // then
        assertThat(result.getNumberOfElements()).isEqualTo(3);
        List<Place> content = result.getContent();
        assertThat(content.get(0).getId()).isEqualTo(place1.getId());
        assertThat(content.get(1).getId()).isEqualTo(place2.getId());
        assertThat(content.get(2).getId()).isEqualTo(place4.getId());
    }

    @DisplayName("내가 북마크한 장소들의 first category에 대한 filtering keywords를 개수가 많은 순으로 추출한다.")
    @Test
    void given_whenGetFilteringKeywordsForFirstCategoryOrderByCountDesc_thenReturnFilteringKeywords() {
        // given
        Member member = memberRepository.save(createNewMember("1"));
        List<Place> places = sut.saveAll(List.of(
                createNewPlace("1", new PlaceCategory("한식", "비빔밥", null)),
                createNewPlace("2", new PlaceCategory("한식", "비빔밥", null)),
                createNewPlace("3", new PlaceCategory("한식", "비빔밥", null)),
                createNewPlace("4", new PlaceCategory("양식", "파스타", null)),
                createNewPlace("5", new PlaceCategory("양식", "파스타", null)),
                createNewPlace("6", new PlaceCategory("양식", "파스타", null)),
                createNewPlace("7", new PlaceCategory("양식", "파스타", null)),
                createNewPlace("8", new PlaceCategory("일식", "라멘", null)),
                createNewPlace("9", new PlaceCategory("분식", null, null)),
                createNewPlace("10", new PlaceCategory("", null, null)),
                createNewPlace("11", new PlaceCategory("", null, null)),
                createNewPlace("12", new PlaceCategory("", null, null))
        ));
        bookmarkRepository.saveAll(places.stream().map(place -> createNewBookmark(member, place)).toList());

        // when
        List<PlaceFilteringKeywordDto> filteringKeywords = sut.getFilteringKeywords(member.getId());

        // then
        List<PlaceFilteringKeywordDto> filteringKeywordsForSecondCategory = filteringKeywords.stream()
                .filter(filteringKeyword -> filteringKeyword.getType().equals(FilteringType.FIRST_CATEGORY))
                .toList();
        assertThat(filteringKeywordsForSecondCategory).hasSize(2);
        assertThat(filteringKeywordsForSecondCategory.get(0))
                .hasFieldOrPropertyWithValue("keyword", "양식")
                .hasFieldOrPropertyWithValue("count", 4)
                .hasFieldOrPropertyWithValue("type", FilteringType.FIRST_CATEGORY);
        assertThat(filteringKeywordsForSecondCategory.get(1))
                .hasFieldOrPropertyWithValue("keyword", "한식")
                .hasFieldOrPropertyWithValue("count", 3)
                .hasFieldOrPropertyWithValue("type", FilteringType.FIRST_CATEGORY);
    }

    @DisplayName("내가 북마크한 장소들의 second category에 대한 filtering keywords를 개수가 많은 순으로 추출한다.")
    @Test
    void given_whenGetFilteringKeywordsForSecondCategoryOrderByCountDesc_thenReturnFilteringKeywords() {
        // given
        Member member = memberRepository.save(createNewMember("1"));
        List<Place> places = sut.saveAll(List.of(
                createNewPlace("1", new PlaceCategory("한식", "비빔밥", null)),
                createNewPlace("2", new PlaceCategory("한식", "비빔밥", null)),
                createNewPlace("3", new PlaceCategory("한식", "비빔밥", null)),
                createNewPlace("4", new PlaceCategory("한식", "국밥", null)),
                createNewPlace("5", new PlaceCategory("한식", "국밥", null)),
                createNewPlace("6", new PlaceCategory("한식", "국밥", null)),
                createNewPlace("7", new PlaceCategory("한식", "국밥", null)),
                createNewPlace("8", new PlaceCategory("한식", "불고기", null)),
                createNewPlace("9", new PlaceCategory("한식", "김밥", null)),
                createNewPlace("10", new PlaceCategory("한식", "", null)),
                createNewPlace("11", new PlaceCategory("한식", "", null)),
                createNewPlace("12", new PlaceCategory("한식", "", null)),
                createNewPlace("13", new PlaceCategory("한식", null, null)),
                createNewPlace("14", new PlaceCategory("한식", null, null)),
                createNewPlace("15", new PlaceCategory("한식", null, null))
        ));
        bookmarkRepository.saveAll(places.stream().map(place -> createNewBookmark(member, place)).toList());

        // when
        List<PlaceFilteringKeywordDto> filteringKeywords = sut.getFilteringKeywords(member.getId());

        // then
        List<PlaceFilteringKeywordDto> filteringKeywordsForSecondCategory = filteringKeywords.stream()
                .filter(filteringKeyword -> filteringKeyword.getType().equals(FilteringType.SECOND_CATEGORY))
                .toList();
        assertThat(filteringKeywordsForSecondCategory).hasSize(2);
        assertThat(filteringKeywordsForSecondCategory.get(0))
                .hasFieldOrPropertyWithValue("keyword", "국밥")
                .hasFieldOrPropertyWithValue("count", 4)
                .hasFieldOrPropertyWithValue("type", FilteringType.SECOND_CATEGORY);
        assertThat(filteringKeywordsForSecondCategory.get(1))
                .hasFieldOrPropertyWithValue("keyword", "비빔밥")
                .hasFieldOrPropertyWithValue("count", 3)
                .hasFieldOrPropertyWithValue("type", FilteringType.SECOND_CATEGORY);
    }

    @DisplayName("내가 북마크한 장소들의 top 3 keywords에 대한 filtering keywords를 개수가 많은 순으로 추출한다.")
    @Test
    void given_whenGetFilteringKeywordsForTop3KeywordsOrderByCountDesc_thenReturnFilteringKeywords() {
        // given
        Member member = memberRepository.save(createNewMember("1"));
        List<Place> places = sut.saveAll(List.of(
                createNewPlace("1", List.of(FRESH, GOOD_PRICE, GOOD_FOR_DATE), "name"),
                createNewPlace("2", List.of(FRESH, BEST_FLAVOR, WITH_ALCOHOL), "name"),
                createNewPlace("3", List.of(FRESH, BEST_FLAVOR, WITH_ALCOHOL), "name"),
                createNewPlace("4", List.of(FRESH, BEST_FLAVOR, WITH_ALCOHOL), "name"),
                createNewPlace("5", List.of(FRESH, BEST_FLAVOR, WITH_ALCOHOL), "name"),
                createNewPlace("6", List.of(FRESH, BEST_FLAVOR, WITH_ALCOHOL), "name"),
                createNewPlace("7", List.of(FRESH, BEST_FLAVOR, WITH_ALCOHOL), "name"),
                createNewPlace("8", List.of(FRESH, BEST_FLAVOR, NOISY), "name"),
                createNewPlace("9", List.of(FRESH, BEST_FLAVOR), "name"),
                createNewPlace("10", List.of(FRESH), "name"),
                createNewPlace("11", List.of(), "name")
        ));
        bookmarkRepository.saveAll(places.stream().map(place -> createNewBookmark(member, place)).toList());

        // when
        List<PlaceFilteringKeywordDto> filteringKeywords = sut.getFilteringKeywords(member.getId());

        // then
        List<PlaceFilteringKeywordDto> filteringKeywordsForTop3Keywords = filteringKeywords.stream()
                .filter(filteringKeyword -> filteringKeyword.getType().equals(FilteringType.TOP_3_KEYWORDS))
                .toList();
        assertThat(filteringKeywordsForTop3Keywords).hasSize(3);
        assertThat(filteringKeywordsForTop3Keywords.get(0))
                .hasFieldOrPropertyWithValue("keyword", FRESH.getContent())
                .hasFieldOrPropertyWithValue("count", 5)
                .hasFieldOrPropertyWithValue("type", FilteringType.TOP_3_KEYWORDS);
        assertThat(filteringKeywordsForTop3Keywords.get(1))
                .hasFieldOrPropertyWithValue("keyword", BEST_FLAVOR.getContent())
                .hasFieldOrPropertyWithValue("count", 4)    // 3.5는 int type variable에 할당되면서 3으로 내림된다.
                .hasFieldOrPropertyWithValue("type", FilteringType.TOP_3_KEYWORDS);
        assertThat(filteringKeywordsForTop3Keywords.get(2))
                .hasFieldOrPropertyWithValue("keyword", WITH_ALCOHOL.getContent())
                .hasFieldOrPropertyWithValue("count", 3)
                .hasFieldOrPropertyWithValue("type", FilteringType.TOP_3_KEYWORDS);
    }

    @DisplayName("내가 북마크한 장소들의 주소(읍면동 단위)에 대한 filtering keywords를 개수가 많은 순으로 추출한다.")
    @Test
    void given_whenGetFilteringKeywordsForAddressOrderByCountDesc_thenReturnFilteringKeywords() {
        // given
        Member member = memberRepository.save(createNewMember("1"));
        List<Place> places = sut.saveAll(List.of(
                createNewPlace("1", Address.of("시 구 연남동 123", null)),
                createNewPlace("2", Address.of("시 구 연남동 234", null)),
                createNewPlace("3", Address.of("시 구 연남동 345", null)),
                createNewPlace("4", Address.of("시 구 연남동 456", null)),
                createNewPlace("5", Address.of("시 구 공덕동 123", null)),
                createNewPlace("6", Address.of("시 구 공덕동 123", null)),
                createNewPlace("7", Address.of("시 구 표선면 세화리 123", null)),
                createNewPlace("8", Address.of("시 구 표선면 세화리 123", null)),
                createNewPlace("9", Address.of("시 구 표선면 세화리 123", null)),
                createNewPlace("10", Address.of("시 구 을지로1가 123", null))
        ));
        bookmarkRepository.saveAll(places.stream().map(place -> createNewBookmark(member, place)).toList());

        // when
        List<PlaceFilteringKeywordDto> filteringKeywords = sut.getFilteringKeywords(member.getId());

        // then
        List<PlaceFilteringKeywordDto> filteringKeywordsForAddress = filteringKeywords.stream()
                .filter(filteringKeyword -> filteringKeyword.getType().equals(FilteringType.ADDRESS))
                .toList();
        assertThat(filteringKeywordsForAddress).hasSize(2);
        assertThat(filteringKeywordsForAddress.get(0))
                .hasFieldOrPropertyWithValue("keyword", "연남동")
                .hasFieldOrPropertyWithValue("count", 4);
        assertThat(filteringKeywordsForAddress.get(1))
                .hasFieldOrPropertyWithValue("keyword", "표선면")
                .hasFieldOrPropertyWithValue("count", 3);
    }

    private Member createNewMember(String socialUid) {
        return Member.of(
                "https://default-profile-image",
                "https://defualt-profile-thumbnail-image",
                socialUid,
                LoginType.KAKAO,
                Set.of(RoleType.USER),
                "test" + socialUid + "@test.com",
                "test" + socialUid,
                null,
                null
        );
    }

    private Place createNewPlace(String kakaoPid, String name) {
        return createNewPlace(kakaoPid, List.of(), name, new PlaceCategory("한식", "냉면", null), new Address("sido", "sgg", "lot number address", "road address"), null, "37", "127", null);
    }

    private Place createNewPlace(String kakaoPid, Address address) {
        return createNewPlace(kakaoPid, List.of(), "test", new PlaceCategory("한식", "냉면", null), address, null, "37", "127", null);
    }

    private Place createNewPlace(String kakaoPid, PlaceCategory category) {
        return createNewPlace(kakaoPid, List.of(), "test", category, new Address("sido", "sigungu", "lot number address", "road address"), null, "37", "127", null);
    }

    private Place createNewPlace(String kakaoPid, List<ReviewKeywordValue> top3Keywords, String name) {
        return createNewPlace(kakaoPid, top3Keywords, name, new PlaceCategory("한식", "냉면", null), new Address("sido", "sgg", "lot number address", "road address"), null, "37", "127", null);
    }

    private Place createNewPlace(String kakaoPid, List<ReviewKeywordValue> top3Keywords, String name, PlaceCategory placeCategory, Address address, String homepageUrl, String lat, String lng, String closingHours) {
        return createPlace(null, top3Keywords, kakaoPid, name, placeCategory, address, homepageUrl, lat, lng, closingHours);
    }

    private Place createPlace(Long id, String kakaoPid, String homepageUrl, String lat, String lng, String closingHours) {
        return createPlace(id, List.of(), kakaoPid, "test", homepageUrl, lat, lng, closingHours);
    }

    private Place createPlace(Long id, List<ReviewKeywordValue> top3Keywords, String kakaoPid, String name, String homepageUrl, String lat, String lng, String closingHours) {
        return createPlace(id, top3Keywords, kakaoPid, name, new PlaceCategory("한식", "냉면", null), new Address("sido", "sgg", "lot number address", "road address"), homepageUrl, lat, lng, closingHours);
    }

    private Place createPlace(Long id, List<ReviewKeywordValue> top3Keywords, String kakaoPid, String name, PlaceCategory placeCategory, Address address, String homepageUrl, String lat, String lng, String closingHours) {
        return Place.of(
                id,
                top3Keywords,
                kakaoPid,
                name,
                "https://page-url",
                KakaoCategoryGroupCode.FD6,
                placeCategory,
                "02-123-4567",
                address,
                homepageUrl,
                new Point(lat, lng),
                closingHours,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private OpeningHours createOpeningHours(Long id, Place place, DayOfWeek dayOfWeek) {
        return OpeningHours.of(
                id,
                place,
                dayOfWeek,
                LocalTime.now().minusHours(6),
                LocalTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private Bookmark createNewBookmark(Member member, Place place) {
        return Bookmark.of(member, place);
    }
}