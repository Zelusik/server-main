package com.zelusik.eatery.unit.domain.place_menus.service;

import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.place.exception.ContainsDuplicateMenusException;
import com.zelusik.eatery.domain.place.exception.PlaceMenusAlreadyExistsException;
import com.zelusik.eatery.domain.place.service.PlaceQueryService;
import com.zelusik.eatery.domain.place_menus.dto.PlaceMenusDto;
import com.zelusik.eatery.domain.place_menus.entity.PlaceMenus;
import com.zelusik.eatery.domain.place_menus.repository.PlaceMenusRepository;
import com.zelusik.eatery.domain.place_menus.service.PlaceMenusCommandService;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.global.scraping.service.WebScrapingService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("[Unit] Service(Command) - Place menus")
@ExtendWith(MockitoExtension.class)
class PlaceMenusCommandServiceTest {

    @InjectMocks
    private PlaceMenusCommandService sut;

    @Mock
    private PlaceQueryService placeQueryService;
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
        given(placeQueryService.findById(placeId)).willReturn(place);
        given(webScrapingService.scrapMenuList(kakaoPid)).willReturn(extractedMenus);
        given(placeMenusRepository.save(any(PlaceMenus.class))).willReturn(expectedResult);

        // when
        PlaceMenusDto actualResult = sut.savePlaceMenus(placeId);

        // then
        then(placeMenusRepository).should().existsByPlace_Id(placeId);
        then(placeQueryService).should().findById(placeId);
        then(webScrapingService).should().scrapMenuList(kakaoPid);
        then(placeMenusRepository).should().save(any(PlaceMenus.class));
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult)
                .hasFieldOrPropertyWithValue("id", placeMenusId)
                .hasFieldOrPropertyWithValue("placeId", placeId)
                .hasFieldOrPropertyWithValue("menus", extractedMenus);
    }

    @DisplayName("장소 메뉴 데이터가 이미 존재하는 상황에서, 장소의 PK가 주어지고, 장소 메뉴 목록을 scraping 및 저장하려고 하면, 예외가 발생한다.")
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
        then(placeQueryService).shouldHaveNoInteractions();
        then(webScrapingService).shouldHaveNoInteractions();
        assertThat(t).isInstanceOf(PlaceMenusAlreadyExistsException.class);
    }

    @DisplayName("장소의 kakao 고유 id가 주어지고, 장소 메뉴 목록을 scraping하고 저장한다.")
    @Test
    void givenKakaoPid_whenSavePlaceMenus_thenReturnPlaceMenus() {
        // given
        long placeId = 1L;
        long placeMenusId = 2L;
        String kakaoPid = "12345";
        Place place = createPlace(placeId, kakaoPid);
        List<String> extractedMenus = List.of("돈까스", "파스타", "수제비", "라면");
        PlaceMenus expectedResult = createPlaceMenus(placeMenusId, place, extractedMenus);
        given(placeMenusRepository.existsByPlace_KakaoPid(kakaoPid)).willReturn(false);
        given(placeQueryService.findByKakaoPid(kakaoPid)).willReturn(place);
        given(webScrapingService.scrapMenuList(kakaoPid)).willReturn(extractedMenus);
        given(placeMenusRepository.save(any(PlaceMenus.class))).willReturn(expectedResult);

        // when
        PlaceMenusDto actualResult = sut.savePlaceMenus(kakaoPid);

        // then
        then(placeMenusRepository).should().existsByPlace_KakaoPid(kakaoPid);
        then(placeQueryService).should().findByKakaoPid(kakaoPid);
        then(webScrapingService).should().scrapMenuList(kakaoPid);
        then(placeMenusRepository).should().save(any(PlaceMenus.class));
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult)
                .hasFieldOrPropertyWithValue("id", placeMenusId)
                .hasFieldOrPropertyWithValue("placeId", placeId)
                .hasFieldOrPropertyWithValue("menus", extractedMenus);
    }

    @DisplayName("장소 메뉴 데이터가 이미 존재하는 상황에서, 장소의 kakao 고유 id가 주어지고, 장소 메뉴 목록을 scraping 및 저장하려고 하면, 예외가 발생한다.")
    @Test
    void givenPlaceMenusAndKakaoPid_whenSavePlaceMenus_thenThrowPlaceMenusAlreadyExistsException() {
        // given
        String kakaoPid = "12345";
        given(placeMenusRepository.existsByPlace_KakaoPid(kakaoPid)).willReturn(true);

        // when
        Throwable t = catchThrowable(() -> sut.savePlaceMenus(kakaoPid));

        // then
        then(placeMenusRepository).should().existsByPlace_KakaoPid(kakaoPid);
        then(placeMenusRepository).shouldHaveNoMoreInteractions();
        then(placeQueryService).shouldHaveNoInteractions();
        then(webScrapingService).shouldHaveNoInteractions();
        assertThat(t).isInstanceOf(PlaceMenusAlreadyExistsException.class);
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
        then(placeQueryService).shouldHaveNoInteractions();
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

    @DisplayName("메뉴와 장소의 PK 값이 주어지고, 기존 메뉴 목록에 새로운 메뉴를 추가하면, 업데이트된 메뉴 목록 정보가 반환된다.")
    @Test
    void givenMenuWithPlaceId_whenAddMenuToPlaceMenus_thenReturnUpdatedPlaceMenus() {
        // given
        long placeId = 1L;
        long placeMenusId = 2L;
        String kakaoPid = "12345";
        Place place = createPlace(placeId, kakaoPid);
        PlaceMenus placeMenus = createPlaceMenus(placeMenusId, place, List.of("돈까스", "파스타", "수제비", "라면"));
        String menuForAdd = "양념 치킨";
        given(placeMenusRepository.findByPlace_Id(placeId)).willReturn(Optional.of(placeMenus));

        // when
        PlaceMenusDto result = sut.addMenu(placeId, menuForAdd);

        // then
        then(placeMenusRepository).should().findByPlace_Id(placeId);
        then(placeMenusRepository).shouldHaveNoMoreInteractions();
        then(placeQueryService).shouldHaveNoInteractions();
        then(webScrapingService).shouldHaveNoInteractions();
        assertThat(result)
                .hasFieldOrPropertyWithValue("id", placeMenusId)
                .hasFieldOrPropertyWithValue("placeId", placeId)
                .hasFieldOrProperty("menus");
        assertThat(result.getMenus()).hasSize(5);
    }

    @DisplayName("장소의 PK 값이 주어지면, 해당하는 장소의 메뉴 목록 데이터를 삭제한다.")
    @Test
    void givenPlaceId_whenDeletePlaceMenus_thenDeleting() {
        // given
        long placeId = 1L;
        willDoNothing().given(placeMenusRepository).deleteByPlace_Id(placeId);

        // when
        sut.delete(placeId);

        // then
        then(placeMenusRepository).should().deleteByPlace_Id(placeId);
        then(placeMenusRepository).shouldHaveNoMoreInteractions();
        then(placeQueryService).shouldHaveNoInteractions();
        then(webScrapingService).shouldHaveNoInteractions();
    }

    private void verifyEveryMocksShouldHaveNoInteractions() {
        then(placeQueryService).shouldHaveNoInteractions();
        then(webScrapingService).shouldHaveNoInteractions();
        then(placeMenusRepository).shouldHaveNoInteractions();
    }

    private void verifyEveryMocksShouldHaveNoMoreInteractions() {
        then(placeQueryService).shouldHaveNoMoreInteractions();
        then(webScrapingService).shouldHaveNoMoreInteractions();
        then(placeMenusRepository).shouldHaveNoMoreInteractions();
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