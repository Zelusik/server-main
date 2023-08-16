package com.zelusik.eatery.unit.service;

import com.zelusik.eatery.constant.place.DayOfWeek;
import com.zelusik.eatery.constant.place.FilteringType;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.place.OpeningHours;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.place.Point;
import com.zelusik.eatery.dto.place.PlaceDto;
import com.zelusik.eatery.dto.place.PlaceFilteringKeywordDto;
import com.zelusik.eatery.dto.place.PlaceScrapingOpeningHourDto;
import com.zelusik.eatery.dto.place.PlaceScrapingResponse;
import com.zelusik.eatery.dto.place.request.PlaceCreateRequest;
import com.zelusik.eatery.exception.place.PlaceAlreadyExistsException;
import com.zelusik.eatery.exception.place.PlaceNotFoundException;
import com.zelusik.eatery.repository.place.OpeningHoursRepository;
import com.zelusik.eatery.repository.place.PlaceRepository;
import com.zelusik.eatery.repository.review.ReviewKeywordRepository;
import com.zelusik.eatery.service.BookmarkService;
import com.zelusik.eatery.service.PlaceService;
import com.zelusik.eatery.service.ReviewImageService;
import com.zelusik.eatery.service.WebScrapingService;
import com.zelusik.eatery.util.PlaceTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.zelusik.eatery.service.PlaceService.MAX_NUM_OF_PLACE_IMAGES;
import static com.zelusik.eatery.util.PlaceTestUtils.createPlace;
import static com.zelusik.eatery.util.PlaceTestUtils.createPlaceDtoWithMarkedStatusAndImages;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Place Service")
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
    private BookmarkService bookmarkService;
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
        PlaceScrapingResponse placeScrapingResponse = PlaceScrapingResponse.of(openingHourDtos, closingHours, homepageUrl);
        PlaceCreateRequest placeCreateRequest = PlaceTestUtils.createPlaceRequest();
        Place expectedResult = PlaceTestUtils.createPlace(placeId, placeCreateRequest.getKakaoPid(), homepageUrl, closingHours);
        given(placeRepository.existsByKakaoPid(placeCreateRequest.getKakaoPid())).willReturn(false);
        given(webScrapingService.getPlaceScrapingInfo(placeCreateRequest.getKakaoPid())).willReturn(placeScrapingResponse);
        given(placeRepository.save(any(Place.class))).willReturn(expectedResult);
        given(openingHoursRepository.saveAll(ArgumentMatchers.<List<OpeningHours>>any())).willReturn(List.of());
        given(bookmarkService.isMarkedPlace(memberId, expectedResult)).willReturn(false);

        // when
        PlaceDto actualResult = sut.create(memberId, placeCreateRequest);

        // then
        then(placeRepository).should().existsByKakaoPid(placeCreateRequest.getKakaoPid());
        then(webScrapingService).should().getPlaceScrapingInfo(placeCreateRequest.getKakaoPid());
        then(placeRepository).should().save(any(Place.class));
        then(openingHoursRepository).should().saveAll(ArgumentMatchers.<List<OpeningHours>>any());
        then(bookmarkService).should().isMarkedPlace(memberId, expectedResult);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult.getKakaoPid()).isEqualTo(placeCreateRequest.getKakaoPid());
    }

    @DisplayName("이미 존재하는 장소 정보가 주어지고, 장소를 생성 및 저장하려고 하면, 예외가 발생한다.")
    @Test
    void givenPlaceRequestInfoWithMemberId_whenCreateAlreadyExistsPlace_thenThrowPlaceAlreadyExistsException() {
        // given
        long memberId = 1L;
        PlaceCreateRequest placeCreateRequest = PlaceTestUtils.createPlaceRequest();
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
        Place expectedResult = PlaceTestUtils.createPlace(placeId, "12345");
        given(placeRepository.findById(placeId)).willReturn(Optional.of(expectedResult));
        given(bookmarkService.isMarkedPlace(memberId, expectedResult)).willReturn(true);
        given(reviewImageService.findLatest3ByPlace(placeId)).willReturn(List.of());

        // when
        PlaceDto actualResult = sut.findDtoWithMarkedStatusAndImagesById(memberId, placeId);

        // then
        then(placeRepository).should().findById(placeId);
        then(bookmarkService).should().isMarkedPlace(memberId, expectedResult);
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
        Place expectedResult = PlaceTestUtils.createPlace(placeId, "12345");
        given(placeRepository.findByKakaoPid(kakaoPid)).willReturn(Optional.of(expectedResult));
        given(bookmarkService.isMarkedPlace(memberId, expectedResult)).willReturn(true);
        given(reviewImageService.findLatest3ByPlace(placeId)).willReturn(List.of());

        // when
        PlaceDto actualResult = sut.findDtoWithMarkedStatusAndImagesByKakaoPid(memberId, kakaoPid);

        // then
        then(placeRepository).should().findByKakaoPid(kakaoPid);
        then(bookmarkService).should().isMarkedPlace(memberId, expectedResult);
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
        Place expectedPlace = PlaceTestUtils.createPlace(1L, kakaoPid);
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
        SliceImpl<PlaceDto> expectedResult = new SliceImpl<>(List.of(createPlaceDtoWithMarkedStatusAndImages(placeId)));
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
        List<Place> expectedResult = List.of(createPlace(placeId, "12345", "강남돈까스", "37", "127", "공휴일"));
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
        SliceImpl<PlaceDto> expectedResult = new SliceImpl<>(List.of(createPlaceDtoWithMarkedStatusAndImages()), pageable, false);
        given(placeRepository.findDtosNearBy(memberId, null, null, point, 50, MAX_NUM_OF_PLACE_IMAGES, pageable)).willReturn(expectedResult);

        // when
        Slice<PlaceDto> actualResult = sut.findDtosNearBy(memberId, null, null, point, pageable);

        // then
        then(placeRepository).should().findDtosNearBy(memberId, null, null, point, 50, MAX_NUM_OF_PLACE_IMAGES, pageable);
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
                        PlaceFilteringKeywordDto.of("연남동", 5, FilteringType.ADDRESS),
                        PlaceFilteringKeywordDto.of("신선한 재료", 3, FilteringType.TOP_3_KEYWORDS)
                ));

        // when
        List<PlaceFilteringKeywordDto> filteringKeywords = sut.getFilteringKeywords(memberId);

        // then
        then(placeRepository).should().getFilteringKeywords(memberId);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(filteringKeywords.size()).isEqualTo(2);
    }

    @DisplayName("장소가 주어지고, 장소의 top 3 keyword를 갱신하면, 업데이트한다.")
    @Test
    void givenPlace_whenRenewTop3Keywords_thenUpdate() {
        // given
        long placeId = 1L;
        Place place = PlaceTestUtils.createPlace(placeId, "1");
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
        then(bookmarkService).shouldHaveNoMoreInteractions();
        then(reviewKeywordRepository).shouldHaveNoMoreInteractions();
    }
}