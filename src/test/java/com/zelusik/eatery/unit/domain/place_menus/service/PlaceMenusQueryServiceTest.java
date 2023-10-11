package com.zelusik.eatery.unit.domain.place_menus.service;

import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.place_menus.dto.PlaceMenusDto;
import com.zelusik.eatery.domain.place_menus.entity.PlaceMenus;
import com.zelusik.eatery.domain.place_menus.exception.PlaceMenusNotFoundByKakaoPidException;
import com.zelusik.eatery.domain.place_menus.exception.PlaceMenusNotFoundByPlaceIdException;
import com.zelusik.eatery.domain.place_menus.repository.PlaceMenusRepository;
import com.zelusik.eatery.domain.place_menus.service.PlaceMenusQueryService;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Service(Query) - Place menus")
@ExtendWith(MockitoExtension.class)
class PlaceMenusQueryServiceTest {

    @InjectMocks
    private PlaceMenusQueryService sut;

    @Mock
    private PlaceMenusRepository placeMenusRepository;

    @DisplayName("장소의 PK 값이 주어지고, 장소 메뉴 데이터를 조회하면, 조회된 결과를 반환한다.")
    @Test
    void givenPlaceId_whenFindPlaceMenus_thenReturnPlaceMenus() {
        // given
        long placeId = 1L;
        long placeMenusId = 2L;
        String kakaoPid = "12345";
        Place place = createPlace(placeId, kakaoPid);
        List<String> extractedMenus = List.of("돈까스", "파스타", "수제비", "라면");
        PlaceMenus expectedResult = createPlaceMenus(placeMenusId, place, extractedMenus);
        given(placeMenusRepository.findByPlace_Id(placeId)).willReturn(Optional.of(expectedResult));

        // when
        PlaceMenusDto actualResult = sut.findDtoByPlaceId(placeId);

        // then
        then(placeMenusRepository).should().findByPlace_Id(placeId);
        then(placeMenusRepository).shouldHaveNoMoreInteractions();
        assertThat(actualResult)
                .hasFieldOrPropertyWithValue("id", placeMenusId)
                .hasFieldOrPropertyWithValue("placeId", placeId)
                .hasFieldOrPropertyWithValue("menus", extractedMenus);
    }

    @DisplayName("장소의 PK 값이 주어지고, 장소 메뉴 데이터를 조회했으나 찾을 수 없는 경우, 예외가 발생한다.")
    @Test
    void givenPlaceId_whenFindNotExistentPlaceMenus_thenThrowPlaceMenusNotFoundByPlaceIdException() {
        // given
        long placeId = 1L;
        given(placeMenusRepository.findByPlace_Id(placeId)).willReturn(Optional.empty());

        // when
        Throwable t = catchThrowable(() -> sut.findDtoByPlaceId(placeId));

        // then
        then(placeMenusRepository).should().findByPlace_Id(placeId);
        then(placeMenusRepository).shouldHaveNoMoreInteractions();
        assertThat(t).isInstanceOf(PlaceMenusNotFoundByPlaceIdException.class);
    }

    @DisplayName("장소의 kakaoPid 값이 주어지고, 장소 메뉴 데이터를 조회하면, 조회된 결과를 반환한다.")
    @Test
    void givenKakaoPid_whenFindPlaceMenus_thenReturnPlaceMenus() {
        // given
        long placeId = 1L;
        long placeMenusId = 2L;
        String kakaoPid = "12345";
        Place place = createPlace(placeId, kakaoPid);
        List<String> extractedMenus = List.of("돈까스", "파스타", "수제비", "라면");
        PlaceMenus expectedResult = createPlaceMenus(placeMenusId, place, extractedMenus);
        given(placeMenusRepository.findByPlace_KakaoPid(kakaoPid)).willReturn(Optional.of(expectedResult));

        // when
        PlaceMenusDto actualResult = sut.findDtoByKakaoPid(kakaoPid);

        // then
        then(placeMenusRepository).should().findByPlace_KakaoPid(kakaoPid);
        then(placeMenusRepository).shouldHaveNoMoreInteractions();
        assertThat(actualResult)
                .hasFieldOrPropertyWithValue("id", placeMenusId)
                .hasFieldOrPropertyWithValue("placeId", placeId)
                .hasFieldOrPropertyWithValue("menus", extractedMenus);
    }

    @DisplayName("장소의 PK 값이 주어지고, 장소 메뉴 데이터를 조회했으나 찾을 수 없는 경우, 예외가 발생한다.")
    @Test
    void givenKakaoPid_whenFindNotExistentPlaceMenus_thenThrowPlaceMenusNotFoundByPlaceIdException() {
        // given
        String kakaoPid = "12345";
        given(placeMenusRepository.findByPlace_KakaoPid(kakaoPid)).willReturn(Optional.empty());

        // when
        Throwable t = catchThrowable(() -> sut.findDtoByKakaoPid(kakaoPid));

        // then
        then(placeMenusRepository).should().findByPlace_KakaoPid(kakaoPid);
        then(placeMenusRepository).shouldHaveNoMoreInteractions();
        assertThat(t).isInstanceOf(PlaceMenusNotFoundByKakaoPidException.class);
    }

    private Place createPlace(long id, String kakaoPid) {
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
                null,
                new Point("37.5595073462493", "126.921462488105"),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private PlaceMenus createPlaceMenus(Long id, Place place, List<String> menus) {
        return PlaceMenus.of(
                id,
                place,
                menus,
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                1L
        );
    }
}