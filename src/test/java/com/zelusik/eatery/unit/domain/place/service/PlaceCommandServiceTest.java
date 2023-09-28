package com.zelusik.eatery.unit.domain.place.service;

import com.zelusik.eatery.domain.bookmark.service.BookmarkQueryService;
import com.zelusik.eatery.domain.opening_hours.entity.OpeningHours;
import com.zelusik.eatery.domain.opening_hours.repository.OpeningHoursRepository;
import com.zelusik.eatery.domain.place.constant.DayOfWeek;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.place.dto.PlaceScrapingInfo;
import com.zelusik.eatery.domain.place.dto.PlaceScrapingOpeningHourDto;
import com.zelusik.eatery.domain.place.dto.request.PlaceCreateRequest;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.place.exception.PlaceAlreadyExistsException;
import com.zelusik.eatery.domain.place.repository.PlaceRepository;
import com.zelusik.eatery.domain.place.service.PlaceCommandService;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review_keyword.repository.ReviewKeywordRepository;
import com.zelusik.eatery.global.scraping.service.WebScrapingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Service(Command) - Place")
@ExtendWith(MockitoExtension.class)
class PlaceCommandServiceTest {

    @InjectMocks
    private PlaceCommandService sut;

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