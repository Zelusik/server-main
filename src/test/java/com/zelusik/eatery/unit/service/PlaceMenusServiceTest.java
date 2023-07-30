package com.zelusik.eatery.unit.service;

import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.place.PlaceMenus;
import com.zelusik.eatery.dto.place.PlaceMenusDto;
import com.zelusik.eatery.exception.place.PlaceMenusNotFoundByPlaceIdException;
import com.zelusik.eatery.repository.place.PlaceMenusRepository;
import com.zelusik.eatery.service.PlaceMenusService;
import com.zelusik.eatery.service.PlaceService;
import com.zelusik.eatery.service.WebScrapingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.zelusik.eatery.util.PlaceTestUtils.createPlace;
import static com.zelusik.eatery.util.PlaceTestUtils.createPlaceMenus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Place Menus Service")
@ExtendWith(MockitoExtension.class)
class PlaceMenusServiceTest {

    @InjectMocks
    private PlaceMenusService sut;

    @Mock
    private PlaceService placeService;
    @Mock
    private PlaceMenusRepository placeMenusRepository;
    @Mock
    private WebScrapingService webScrapingService;

    @DisplayName("장소의 PK가 주어지고, 장소 메뉴 목록을 scraping하고 저장한다.")
    @Test
    void givenPlaceId_whenSavePlaceMenus_thenReturnPlaceMenus() {
        // given
        long placeId = 1L;
        long placeMenusId = 2L;
        String kakaoPid = "12345";
        Place place = createPlace(placeId, kakaoPid);
        List<String> extractedMenus = List.of("돈까스", "파스타", "수제비", "라면");
        PlaceMenus expectedResult = createPlaceMenus(placeMenusId, place, extractedMenus);
        given(placeService.findById(placeId)).willReturn(place);
        given(webScrapingService.scrapMenuList(kakaoPid)).willReturn(extractedMenus);
        given(placeMenusRepository.save(any(PlaceMenus.class))).willReturn(expectedResult);

        // when
        PlaceMenusDto actualResult = sut.savePlaceMenus(placeId);

        // then
        then(placeService).should().findById(placeId);
        then(webScrapingService).should().scrapMenuList(kakaoPid);
        then(placeMenusRepository).should().save(any(PlaceMenus.class));
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult)
                .hasFieldOrPropertyWithValue("id", placeMenusId)
                .hasFieldOrPropertyWithValue("placeId", placeId)
                .hasFieldOrPropertyWithValue("menus", extractedMenus);
    }

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
        then(placeService).shouldHaveNoInteractions();
        then(webScrapingService).shouldHaveNoInteractions();
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
        then(placeService).shouldHaveNoInteractions();
        then(webScrapingService).shouldHaveNoInteractions();
        assertThat(t).isInstanceOf(PlaceMenusNotFoundByPlaceIdException.class);
    }

    private void verifyEveryMocksShouldHaveNoMoreInteractions() {
        then(placeService).shouldHaveNoMoreInteractions();
        then(webScrapingService).shouldHaveNoMoreInteractions();
        then(placeMenusRepository).shouldHaveNoMoreInteractions();
    }
}