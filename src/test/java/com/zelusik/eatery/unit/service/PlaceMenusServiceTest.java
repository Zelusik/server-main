package com.zelusik.eatery.unit.service;

import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.place.PlaceMenus;
import com.zelusik.eatery.dto.place.PlaceMenusDto;
import com.zelusik.eatery.exception.place.ContainsDuplicateMenusException;
import com.zelusik.eatery.exception.place.PlaceMenusAlreadyExistsException;
import com.zelusik.eatery.exception.place.PlaceMenusNotFoundException;
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
        given(placeMenusRepository.existsByPlace_Id(placeId)).willReturn(false);
        given(placeService.findById(placeId)).willReturn(place);
        given(webScrapingService.scrapMenuList(kakaoPid)).willReturn(extractedMenus);
        given(placeMenusRepository.save(any(PlaceMenus.class))).willReturn(expectedResult);

        // when
        PlaceMenusDto actualResult = sut.savePlaceMenus(placeId);

        // then
        then(placeMenusRepository).should().existsByPlace_Id(placeId);
        then(placeService).should().findById(placeId);
        then(webScrapingService).should().scrapMenuList(kakaoPid);
        then(placeMenusRepository).should().save(any(PlaceMenus.class));
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult)
                .hasFieldOrPropertyWithValue("id", placeMenusId)
                .hasFieldOrPropertyWithValue("placeId", placeId)
                .hasFieldOrPropertyWithValue("menus", extractedMenus);
    }

    @DisplayName("장소 메뉴 데이터가 이미 존재하는 상황에서, 장소 메뉴 목록을 scraping 및 저장하려고 하면, 예외가 발생한다.")
    @Test
    void givenPlaceMenusAndPlaceId_whenSavePlaceMenus_thenThrowPlaceMenusAlreadyExistsException() {
        // given
        long placeId = 1L;
        given(placeMenusRepository.existsByPlace_Id(placeId)).willReturn(true);

        // when
        Throwable t = catchThrowable(() -> sut.savePlaceMenus(placeId));

        // then
        then(placeMenusRepository).should().existsByPlace_Id(placeId);
        then(placeMenusRepository).shouldHaveNoMoreInteractions();
        then(placeService).shouldHaveNoInteractions();
        then(webScrapingService).shouldHaveNoInteractions();
        assertThat(t).isInstanceOf(PlaceMenusAlreadyExistsException.class);
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
        assertThat(t).isInstanceOf(PlaceMenusNotFoundException.class);
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
        then(placeService).shouldHaveNoInteractions();
        then(webScrapingService).shouldHaveNoInteractions();
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
        then(placeService).shouldHaveNoInteractions();
        then(webScrapingService).shouldHaveNoInteractions();
        assertThat(t).isInstanceOf(PlaceMenusNotFoundException.class);
    }

    @DisplayName("메뉴 목록과 장소의 PK 값이 주어지고, 메뉴 목록을 업데이트하면, 업데이트된 메뉴 목록 정보가 반환된다.")
    @Test
    void givenMenusWithPlaceId_whenUpdateMenus_thenReturnUpdatedPlaceMenus() {
        // given
        long placeId = 1L;
        long placeMenusId = 2L;
        String kakaoPid = "12345";
        Place place = createPlace(placeId, kakaoPid);
        PlaceMenus placeMenus = createPlaceMenus(placeMenusId, place, List.of("돈까스", "파스타", "수제비", "라면"));
        List<String> menusForUpdate = List.of("치킨");
        given(placeMenusRepository.findByPlace_Id(placeId)).willReturn(Optional.of(placeMenus));

        // when
        PlaceMenusDto updatedPlaceMenusDto = sut.updateMenus(placeId, menusForUpdate);

        // then
        then(placeMenusRepository).should().findByPlace_Id(placeId);
        then(placeMenusRepository).shouldHaveNoMoreInteractions();
        then(placeService).shouldHaveNoInteractions();
        then(webScrapingService).shouldHaveNoInteractions();
        assertThat(updatedPlaceMenusDto)
                .hasFieldOrPropertyWithValue("id", placeMenusId)
                .hasFieldOrPropertyWithValue("placeId", placeId)
                .hasFieldOrPropertyWithValue("menus", menusForUpdate);
    }

    @DisplayName("중복된 메뉴가 존재하는 메뉴 목록이 주어지고, 메뉴 목록을 업데이트하면, 예외가 발생한다.")
    @Test
    void givenMenusWhereDuplicateMenusExist_whenUpdateMenus_thenThrowContainsDuplicateMenusException() {
        // given
        long placeId = 1L;
        List<String> menusForUpdate = List.of("양념치킨", "양념 치킨");

        // when
        Throwable t = catchThrowable(() -> sut.updateMenus(placeId, menusForUpdate));

        // then
        verifyEveryMocksShouldHaveNoInteractions();
        assertThat(t).isInstanceOf(ContainsDuplicateMenusException.class);
    }

    private void verifyEveryMocksShouldHaveNoInteractions() {
        then(placeService).shouldHaveNoInteractions();
        then(webScrapingService).shouldHaveNoInteractions();
        then(placeMenusRepository).shouldHaveNoInteractions();
    }

    private void verifyEveryMocksShouldHaveNoMoreInteractions() {
        then(placeService).shouldHaveNoMoreInteractions();
        then(webScrapingService).shouldHaveNoMoreInteractions();
        then(placeMenusRepository).shouldHaveNoMoreInteractions();
    }
}