package com.zelusik.eatery.unit.domain.place.service;

import com.zelusik.eatery.domain.bookmark.service.BookmarkQueryService;
import com.zelusik.eatery.domain.opening_hours.entity.OpeningHours;
import com.zelusik.eatery.domain.opening_hours.repository.OpeningHoursRepository;
import com.zelusik.eatery.domain.place.constant.DayOfWeek;
import com.zelusik.eatery.domain.place.constant.FilteringType;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.place.dto.PlaceFilteringKeywordDto;
import com.zelusik.eatery.domain.place.dto.PlaceScrapingInfo;
import com.zelusik.eatery.domain.place.dto.PlaceScrapingOpeningHourDto;
import com.zelusik.eatery.domain.place.dto.request.FindNearPlacesFilteringConditionRequest;
import com.zelusik.eatery.domain.place.dto.request.PlaceCreateRequest;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.place.exception.PlaceAlreadyExistsException;
import com.zelusik.eatery.domain.place.exception.PlaceNotFoundException;
import com.zelusik.eatery.domain.place.repository.PlaceRepository;
import com.zelusik.eatery.domain.place.service.PlaceService;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review_image.service.ReviewImageService;
import com.zelusik.eatery.domain.review_keyword.repository.ReviewKeywordRepository;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import com.zelusik.eatery.global.scraping.service.WebScrapingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.zelusik.eatery.domain.place.constant.DayOfWeek.*;
import static com.zelusik.eatery.domain.place.service.PlaceService.MAX_NUM_OF_PLACE_IMAGES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Service - Place")
@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @InjectMocks
    private PlaceService sut;

    @Mock
    private ReviewImageService reviewImageService;
    @Mock
    private WebScrapingService webScrapingService;
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private OpeningHoursRepository openingHoursRepository;
    @Mock
    private BookmarkQueryService bookmarkQueryService;
    @Mock
    private ReviewKeywordRepository reviewKeywordRepository;

    @DisplayName("영업시간이 포함된 장소 정보가 주어지면 장소를 생성 및 저장하고 저장된 장소를 반환한다.")
    @Test
    void givenPlaceRequestInfoWithOpeningHours_whenCreatePlace_thenReturnSavedPlace() {
        // given
        long memberId = 1L;
        long placeId = 2L;
        List<PlaceScrapingOpeningHourDto> openingHourDtos = List.of(
                PlaceScrapingOpeningHourDto.of(DayOfWeek.MON, LocalTime.of(12, 0), LocalTime.of(22, 0)),
                PlaceScrapingOpeningHourDto.of(DayOfWeek.TUE, LocalTime.of(12, 0), LocalTime.of(22, 0)),
                PlaceScrapingOpeningHourDto.of(DayOfWeek.WED, LocalTime.of(12, 0), LocalTime.of(22, 0)),
                PlaceScrapingOpeningHourDto.of(DayOfWeek.THU, LocalTime.of(12, 0), LocalTime.of(22, 0)),
                PlaceScrapingOpeningHourDto.of(DayOfWeek.FRI, LocalTime.of(12, 0), LocalTime.of(22, 0))
        );
        String closingHours = "토요일\n일요일";
        String homepageUrl = "https://homepage.url";
        PlaceScrapingInfo placeScrapingInfo = PlaceScrapingInfo.of(openingHourDtos, closingHours, homepageUrl);
        PlaceCreateRequest placeCreateRequest = createPlaceRequest();
        Place expectedResult = createPlace(placeId, placeCreateRequest.getKakaoPid(), homepageUrl, closingHours);
        given(placeRepository.existsByKakaoPid(placeCreateRequest.getKakaoPid())).willReturn(false);
        given(webScrapingService.getPlaceScrapingInfo(placeCreateRequest.getKakaoPid())).willReturn(placeScrapingInfo);
        given(placeRepository.save(any(Place.class))).willReturn(expectedResult);
        given(openingHoursRepository.saveAll(ArgumentMatchers.<List<OpeningHours>>any())).willReturn(List.of());
        given(bookmarkQueryService.isMarkedPlace(memberId, expectedResult)).willReturn(false);

        // when
        PlaceDto actualResult = sut.create(memberId, placeCreateRequest);

        // then
        then(placeRepository).should().existsByKakaoPid(placeCreateRequest.getKakaoPid());
        then(webScrapingService).should().getPlaceScrapingInfo(placeCreateRequest.getKakaoPid());
        then(placeRepository).should().save(any(Place.class));
        then(openingHoursRepository).should().saveAll(ArgumentMatchers.<List<OpeningHours>>any());
        then(bookmarkQueryService).should().isMarkedPlace(memberId, expectedResult);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult.getKakaoPid()).isEqualTo(placeCreateRequest.getKakaoPid());
    }

    @DisplayName("이미 존재하는 장소 정보가 주어지고, 장소를 생성 및 저장하려고 하면, 예외가 발생한다.")
    @Test
    void givenPlaceRequestInfoWithMemberId_whenCreateAlreadyExistsPlace_thenThrowPlaceAlreadyExistsException() {
        // given
        long memberId = 1L;
        PlaceCreateRequest placeCreateRequest = createPlaceRequest();
        given(placeRepository.existsByKakaoPid(placeCreateRequest.getKakaoPid())).willReturn(true);

        // when
        Throwable t = catchThrowable(() -> sut.create(memberId, placeCreateRequest));

        // then
        then(placeRepository).should().existsByKakaoPid(placeCreateRequest.getKakaoPid());
        then(placeRepository).shouldHaveNoMoreInteractions();
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(t).isInstanceOf(PlaceAlreadyExistsException.class);
    }

    @DisplayName("Id(PK)가 주어지고, 일치하는 장소를 찾으면, 장소 정보를 반환한다.")
    @Test
    void givenId_whenFindExistentPlace_thenReturnPlaceDto() {
        // given
        long placeId = 1L;
        long memberId = 2L;
        Place expectedResult = createPlace(placeId, "12345");
        given(placeRepository.findById(placeId)).willReturn(Optional.of(expectedResult));
        given(bookmarkQueryService.isMarkedPlace(memberId, expectedResult)).willReturn(true);
        given(reviewImageService.findLatest3ByPlace(placeId)).willReturn(List.of());

        // when
        PlaceDto actualResult = sut.findDtoWithMarkedStatusAndImagesById(memberId, placeId);

        // then
        then(placeRepository).should().findById(placeId);
        then(bookmarkQueryService).should().isMarkedPlace(memberId, expectedResult);
        then(reviewImageService).should().findLatest3ByPlace(placeId);
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
        Throwable t = catchThrowable(() -> sut.findDtoWithMarkedStatusAndImagesById(memberId, placeId));

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
        given(reviewImageService.findLatest3ByPlace(placeId)).willReturn(List.of());

        // when
        PlaceDto actualResult = sut.findDtoWithMarkedStatusAndImagesByKakaoPid(memberId, kakaoPid);

        // then
        then(placeRepository).should().findByKakaoPid(kakaoPid);
        then(bookmarkQueryService).should().isMarkedPlace(memberId, expectedResult);
        then(reviewImageService).should().findLatest3ByPlace(placeId);
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
        Throwable t = catchThrowable(() -> sut.findDtoWithMarkedStatusAndImagesByKakaoPid(1L, kakaoPid));

        // then
        then(placeRepository).should().findByKakaoPid(kakaoPid);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(t).isInstanceOf(PlaceNotFoundException.class);
    }

    @DisplayName("kakaoPid가 주어지고, 일치하는 장소를 찾으면, 장소를 반환한다.")
    @Test
    void givenKakaoPid_whenFindExistentPlace_thenReturnOptionalPlace() {
        // given
        String kakaoPid = "1";
        Place expectedPlace = createPlace(1L, kakaoPid);
        given(placeRepository.findByKakaoPid(kakaoPid)).willReturn(Optional.of(expectedPlace));

        // when
        Optional<Place> actualPlace = sut.findOptByKakaoPid(kakaoPid);

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
        Optional<Place> actualPlace = sut.findOptByKakaoPid(kakaoPid);

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
        PageImpl<PlaceDto> expectedResult = new PageImpl<>(List.of(createPlaceDto(placeId)));
        given(placeRepository.findMarkedPlaces(memberId, filteringType, filteringKeyword, MAX_NUM_OF_PLACE_IMAGES, pageable)).willReturn(expectedResult);

        // when
        Slice<PlaceDto> actualResult = sut.findMarkedDtos(memberId, filteringType, filteringKeyword, pageable);

        // then
        then(placeRepository).should().findMarkedPlaces(memberId, filteringType, filteringKeyword, MAX_NUM_OF_PLACE_IMAGES, pageable);
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
        Page<PlaceDto> expectedResult = new PageImpl<>(List.of(createPlaceDto(2L)), pageable, 1);
        given(placeRepository.findDtosNearBy(memberId, filteringCondition, point, 50, MAX_NUM_OF_PLACE_IMAGES, pageable)).willReturn(expectedResult);

        // when
        Slice<PlaceDto> actualResult = sut.findDtosNearBy(memberId, filteringCondition, point, pageable);

        // then
        then(placeRepository).should().findDtosNearBy(memberId, filteringCondition, point, 50, MAX_NUM_OF_PLACE_IMAGES, pageable);
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

    @DisplayName("장소가 주어지고, 장소의 top 3 keyword를 갱신하면, 업데이트한다.")
    @Test
    void givenPlace_whenRenewTop3Keywords_thenUpdate() {
        // given
        long placeId = 1L;
        Place place = createPlace(placeId, "1");
        List<ReviewKeywordValue> top3Keywords = List.of(
                ReviewKeywordValue.BEST_FLAVOR,
                ReviewKeywordValue.GOOD_FOR_DATE,
                ReviewKeywordValue.GOOD_PRICE
        );
        given(reviewKeywordRepository.searchTop3Keywords(placeId)).willReturn(top3Keywords);

        // when
        sut.renewTop3Keywords(place);

        // then
        then(reviewKeywordRepository).should().searchTop3Keywords(placeId);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(place.getTop3Keywords().size()).isEqualTo(3);
        assertThat(place.getTop3Keywords().get(0)).isEqualTo(ReviewKeywordValue.BEST_FLAVOR);
        assertThat(place.getTop3Keywords().get(1)).isEqualTo(ReviewKeywordValue.GOOD_FOR_DATE);
        assertThat(place.getTop3Keywords().get(2)).isEqualTo(ReviewKeywordValue.GOOD_PRICE);
    }

    private void verifyEveryMocksShouldHaveNoMoreInteractions() {
        then(reviewImageService).shouldHaveNoMoreInteractions();
        then(webScrapingService).shouldHaveNoMoreInteractions();
        then(placeRepository).shouldHaveNoMoreInteractions();
        then(openingHoursRepository).shouldHaveNoMoreInteractions();
        then(bookmarkQueryService).shouldHaveNoMoreInteractions();
        then(reviewKeywordRepository).shouldHaveNoMoreInteractions();
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

    private PlaceCreateRequest createPlaceRequest() {
        return PlaceCreateRequest.of(
                "308342289",
                "연남토마 본점",
                "http://place.map.kakao.com/308342289",
                KakaoCategoryGroupCode.FD6,
                "음식점 > 퓨전요리 > 퓨전일식",
                "02-332-8064",
                "서울 마포구 연남동 568-26",
                "서울 마포구 월드컵북로6길 61",
                "37.5595073462493",
                "126.921462488105"
        );
    }
}