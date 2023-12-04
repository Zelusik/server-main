package com.zelusik.eatery.unit.domain.place.service;

import com.zelusik.eatery.domain.bookmark.service.BookmarkQueryService;
import com.zelusik.eatery.domain.place.constant.FilteringType;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.place.dto.PlaceFilteringKeywordDto;
import com.zelusik.eatery.domain.place.dto.PlaceWithMarkedStatusAndImagesDto;
import com.zelusik.eatery.domain.place.dto.request.FindNearPlacesFilteringConditionRequest;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.place.exception.PlaceNotFoundByKakaoPidException;
import com.zelusik.eatery.domain.place.exception.PlaceNotFoundException;
import com.zelusik.eatery.domain.place.repository.PlaceRepository;
import com.zelusik.eatery.domain.place.service.PlaceQueryService;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review_image.service.ReviewImageQueryService;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.zelusik.eatery.domain.place.constant.DayOfWeek.*;
import static com.zelusik.eatery.domain.place.service.PlaceQueryService.MAX_NUM_OF_PLACE_IMAGES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Service(Query) - Place")
@ExtendWith(MockitoExtension.class)
class PlaceQueryServiceTest {

    @InjectMocks
    private PlaceQueryService sut;

    @Mock
    private ReviewImageQueryService reviewImageQueryService;
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private BookmarkQueryService bookmarkQueryService;

    @DisplayName("Id(PK)가 주어지고, 일치하는 장소를 찾으면, 장소 정보를 반환한다.")
    @Test
    void givenId_whenFindExistentPlace_thenReturnPlaceDto() {
        // given
        long placeId = 1L;
        long memberId = 2L;
        Place expectedResult = createPlace(placeId, "12345");
        given(placeRepository.findById(placeId)).willReturn(Optional.of(expectedResult));
        given(bookmarkQueryService.isMarkedPlace(memberId, expectedResult)).willReturn(true);
        given(reviewImageQueryService.findLatest3ByPlace(placeId)).willReturn(List.of());

        // when
        PlaceDto actualResult = sut.getDtoWithMarkedStatusAndImagesById(memberId, placeId);

        // then
        then(placeRepository).should().findById(placeId);
        then(bookmarkQueryService).should().isMarkedPlace(memberId, expectedResult);
        then(reviewImageQueryService).should().findLatest3ByPlace(placeId);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult).hasFieldOrPropertyWithValue("id", placeId);
    }

    @DisplayName("Id(PK)가 주어지고, 존재하지 않는 장소를 찾으면, 예외가 발생한다.")
    @Test
    void givenId_whenFindNotExistentPlace_thenThrowPlaceNotFoundException() {
        // given
        long placeId = 1L;
        long memberId = 2L;
        given(placeRepository.findById(placeId)).willReturn(Optional.empty());

        // when
        Throwable t = catchThrowable(() -> sut.getDtoWithMarkedStatusAndImagesById(memberId, placeId));

        // then
        then(placeRepository).should().findById(placeId);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(t).isInstanceOf(PlaceNotFoundException.class);
    }

    @DisplayName("kakaoPid가 주어지고, 해당하는 장소를 찾으면, 장소 정보를 반환한다.")
    @Test
    void givenKakaoPid_whenFindPlace_thenReturnPlaceDto() {
        // given
        long placeId = 1L;
        long memberId = 2L;
        String kakaoPid = "12345";
        Place expectedResult = createPlace(placeId, "12345");
        given(placeRepository.findByKakaoPid(kakaoPid)).willReturn(Optional.of(expectedResult));
        given(bookmarkQueryService.isMarkedPlace(memberId, expectedResult)).willReturn(true);
        given(reviewImageQueryService.findLatest3ByPlace(placeId)).willReturn(List.of());

        // when
        PlaceDto actualResult = sut.getDtoWithMarkedStatusAndImagesByKakaoPid(memberId, kakaoPid);

        // then
        then(placeRepository).should().findByKakaoPid(kakaoPid);
        then(bookmarkQueryService).should().isMarkedPlace(memberId, expectedResult);
        then(reviewImageQueryService).should().findLatest3ByPlace(placeId);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult)
                .hasFieldOrPropertyWithValue("id", placeId)
                .hasFieldOrPropertyWithValue("kakaoPid", kakaoPid);
    }

    @DisplayName("kakaoPid가 주어지고, 해당하는 장소를 찾았으나 존재하지 않는다면, 예외가 발생한다.")
    @Test
    void givenKakaoPid_whenFindNotExistentPlace_thenThrowPlaceNotFoundException() {
        // given
        String kakaoPid = "12345";
        given(placeRepository.findByKakaoPid(kakaoPid)).willReturn(Optional.empty());

        // when
        Throwable t = catchThrowable(() -> sut.getDtoWithMarkedStatusAndImagesByKakaoPid(1L, kakaoPid));

        // then
        then(placeRepository).should().findByKakaoPid(kakaoPid);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(t).isInstanceOf(PlaceNotFoundByKakaoPidException.class);
    }

    @DisplayName("kakaoPid가 주어지고, 일치하는 장소를 찾으면, 장소를 반환한다.")
    @Test
    void givenKakaoPid_whenFindExistentPlace_thenReturnOptionalPlace() {
        // given
        String kakaoPid = "1";
        Place expectedPlace = createPlace(1L, kakaoPid);
        given(placeRepository.findByKakaoPid(kakaoPid)).willReturn(Optional.of(expectedPlace));

        // when
        Optional<Place> actualPlace = sut.findByKakaoPid(kakaoPid);

        // then
        then(placeRepository).should().findByKakaoPid(kakaoPid);
        assertThat(actualPlace.get().getId()).isEqualTo(expectedPlace.getId());
    }

    @DisplayName("kakaoPid가 주어지고, kakaoPid에 일치하는 장소가 없댜면, 비어있는 Optional을 반환한다.")
    @Test
    void givenKakaoPid_whenFindNotExistentPlace_thenReturnEmptyOptional() {
        // given
        String kakaoPid = "test";
        given(placeRepository.findByKakaoPid(kakaoPid)).willReturn(Optional.empty());

        // when
        Optional<Place> actualPlace = sut.findByKakaoPid(kakaoPid);

        // then
        then(placeRepository).should().findByKakaoPid(kakaoPid);
        assertThat(actualPlace).isEmpty();
    }

    @DisplayName("필터링 조건이 주어지고, 북마크에 저장한 장소들을 조회하면, 저장된 장소들이 반환된다.")
    @Test
    void givenFilteringCondition_whenFindingMarkedPlaces_thenReturnMarkedPlaces() {
        // given
        long memberId = 1L;
        long placeId = 2L;
        FilteringType filteringType = FilteringType.SECOND_CATEGORY;
        String filteringKeyword = "고기,육류";
        Pageable pageable = Pageable.ofSize(30);
        PageImpl<PlaceWithMarkedStatusAndImagesDto> expectedResult = new PageImpl<>(List.of(createPlaceWithMarkedStatusAndImagesDto(placeId)));
        given(placeRepository.findMarkedDtosWithoutOpeningHours(memberId, filteringType, filteringKeyword, MAX_NUM_OF_PLACE_IMAGES, pageable)).willReturn(expectedResult);

        // when
        Slice<PlaceWithMarkedStatusAndImagesDto> actualResult = sut.findMarkedDtosWithoutOpeningHours(memberId, filteringType, filteringKeyword, pageable);

        // then
        then(placeRepository).should().findMarkedDtosWithoutOpeningHours(memberId, filteringType, filteringKeyword, MAX_NUM_OF_PLACE_IMAGES, pageable);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult.getSize()).isEqualTo(expectedResult.getSize());
        assertThat(actualResult.getContent().get(0).getId()).isEqualTo(placeId);
    }

    @DisplayName("검색 키워드가 주어지고, 키워드로 장소를 검색하면, 검색된 장소들이 반환된다.")
    @Test
    void givenSearchKeyword_whenSearching_thenReturnSearchedPlaces() {
        // given
        String searchKeyword = "강남";
        Pageable pageable = Pageable.ofSize(30);
        long placeId = 1L;
        List<Place> expectedResult = List.of(createPlace(placeId, "12345", null, "공휴일"));
        given(placeRepository.searchByKeyword(searchKeyword, pageable)).willReturn(new SliceImpl<>(expectedResult, pageable, false));

        // when
        Slice<PlaceDto> actualResult = sut.searchDtosByKeyword(searchKeyword, pageable);

        // then
        then(placeRepository).should().searchByKeyword(searchKeyword, pageable);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult.getNumberOfElements()).isEqualTo(expectedResult.size());
        assertThat(actualResult.getContent()).hasSize(expectedResult.size());
        for (int i = 0; i < expectedResult.size(); i++) {
            assertThat(actualResult.getContent().get(i).getId()).isEqualTo(expectedResult.get(i).getId());
        }
    }

    @DisplayName("장소들이 존재하고, 중심 좌표 근처의 장소를 조회하면, 거리순으로 정렬된 장소 목록을 반환한다.")
    @Test
    void givenPlaces_whenFindNearBy_thenReturnPlaceSliceSortedByDistance() {
        // given
        long memberId = 1L;
        Point point = new Point("37", "127");
        Pageable pageable = Pageable.ofSize(30);
        FindNearPlacesFilteringConditionRequest filteringCondition = new FindNearPlacesFilteringConditionRequest(
                FoodCategoryValue.KOREAN,
                List.of(MON, WED, FRI),
                ReviewKeywordValue.WITH_ALCOHOL,
                false
        );
        Page<PlaceWithMarkedStatusAndImagesDto> expectedResult = new PageImpl<>(List.of(createPlaceWithMarkedStatusAndImagesDto(2L)), pageable, 1);
        given(placeRepository.findDtosWithoutOpeningHoursNearBy(memberId, filteringCondition, point, 50, MAX_NUM_OF_PLACE_IMAGES, pageable)).willReturn(expectedResult);

        // when
        Slice<PlaceWithMarkedStatusAndImagesDto> actualResult = sut.findDtosWithoutOpeningHoursNearBy(memberId, filteringCondition, point, pageable);

        // then
        then(placeRepository).should().findDtosWithoutOpeningHoursNearBy(memberId, filteringCondition, point, 50, MAX_NUM_OF_PLACE_IMAGES, pageable);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult.getSize()).isEqualTo(expectedResult.getSize());
        assertThat(actualResult.getContent().get(0).getId()).isEqualTo(expectedResult.getContent().get(0).getId());
    }

    @DisplayName("내가 저장한 장소들에 대한 filtering keyword를 조회하면, 검색된 결과(List)가 반환된다.")
    @Test
    void given_whenGetFilteringKeywords_thenReturnFilteringKeywords() {
        // given
        long memberId = 1L;
        given(placeRepository.getFilteringKeywords(memberId))
                .willReturn(List.of(
                        new PlaceFilteringKeywordDto("연남동", 5, FilteringType.ADDRESS),
                        new PlaceFilteringKeywordDto("신선한 재료", 3, FilteringType.TOP_3_KEYWORDS)
                ));

        // when
        List<PlaceFilteringKeywordDto> filteringKeywords = sut.getFilteringKeywords(memberId);

        // then
        then(placeRepository).should().getFilteringKeywords(memberId);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(filteringKeywords.size()).isEqualTo(2);
    }

    @DisplayName("kakao place id가 주어지고, 주어진 kakao place id로 장소의 DB 존재 여부를 조회하면, 조회된 결과를 반환한다.")
    @Test
    void givenKakaoPid_whenGetExistenceOfPlaceByKakaoPid_thenReturnExistenceOfPlace() {
        // given
        String kakaoPid = "12345";
        boolean expectedResult = true;
        given(placeRepository.existsByKakaoPid(kakaoPid)).willReturn(expectedResult);

        // when
        boolean actualResult = sut.existsByKakaoPid(kakaoPid);

        // then
        then(placeRepository).should().existsByKakaoPid(kakaoPid);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    private void verifyEveryMocksShouldHaveNoMoreInteractions() {
        then(reviewImageQueryService).shouldHaveNoMoreInteractions();
        then(placeRepository).shouldHaveNoMoreInteractions();
        then(bookmarkQueryService).shouldHaveNoMoreInteractions();
    }

    private Place createPlace(long id, String kakaoPid, String homepageUrl, String closingHours) {
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
                homepageUrl,
                new Point("37.5595073462493", "126.921462488105"),
                closingHours,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private Place createPlace(long id, String kakaoPid) {
        return createPlace(id, kakaoPid, null, null);
    }

    private PlaceWithMarkedStatusAndImagesDto createPlaceWithMarkedStatusAndImagesDto(long placeId) {
        return new PlaceWithMarkedStatusAndImagesDto(
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
                false,
                List.of()
        );
    }
}