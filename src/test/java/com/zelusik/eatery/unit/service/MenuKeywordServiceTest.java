package com.zelusik.eatery.unit.service;

import com.zelusik.eatery.constant.MenuKeywordCategory;
import com.zelusik.eatery.domain.MenuKeyword;
import com.zelusik.eatery.domain.place.PlaceCategory;
import com.zelusik.eatery.dto.menu_keyword.response.MenuKeywordResponse;
import com.zelusik.eatery.repository.menu_keyword.MenuKeywordRepository;
import com.zelusik.eatery.service.MenuKeywordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.zelusik.eatery.constant.MenuKeywordCategory.MENU_NAME;
import static com.zelusik.eatery.constant.MenuKeywordCategory.PLACE_CATEGORY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Menu keyword service")
@ExtendWith(MockitoExtension.class)
class MenuKeywordServiceTest {

    @InjectMocks
    private MenuKeywordService sut;

    @Mock
    private MenuKeywordRepository menuKeywordRepository;

    @DisplayName("메뉴 키워드 조회 - 메뉴 이름에 대해 조회된 키워드가 10개 이상인 경우")
    @Test
    void givenTenOrMoreKeywordsForMenuName_whenGetKeywords_thenReturnKeywords() {
        // given
        String menuName = "새우버거";
        EnumMap<MenuKeywordCategory, List<String>> namesMap = new EnumMap<>(Map.of(
                MENU_NAME, List.of("새우", "버거"),
                PLACE_CATEGORY, List.of()
        ));
        List<MenuKeyword> menuKeywordsFromNamesForMenuName = List.of(
                MenuKeyword.of(MENU_NAME, "새우", List.of("1", "2", "3", "4", "5", "6")),
                MenuKeyword.of(MENU_NAME, "버거", List.of("7", "8", "9", "10", "11", "12"))
        );
        List<MenuKeywordResponse> expectedResults = List.of(new MenuKeywordResponse(menuName, List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")));
        given(menuKeywordRepository.getAllByNames(namesMap.get(MENU_NAME))).willReturn(menuKeywordsFromNamesForMenuName);

        // when
        List<MenuKeywordResponse> actualResults = sut.getKeywords(
                new PlaceCategory("first", null, null),
                List.of(menuName),
                namesMap,
                List.of("100", "101", "102")
        );

        // then
        then(menuKeywordRepository).should().getAllByNames(namesMap.get(MENU_NAME));
        then(menuKeywordRepository).shouldHaveNoMoreInteractions();
        assertThat(actualResults).isNotEmpty();
        assertThat(actualResults.size()).isEqualTo(expectedResults.size());
        for (int i = 0; i < expectedResults.size(); i++) {
            MenuKeywordResponse expectedResult = expectedResults.get(i);
            MenuKeywordResponse actualResult = actualResults.get(i);
            assertThat(actualResult.getMenu()).isEqualTo(expectedResult.getMenu());
            assertIterableEquals(expectedResult.getKeywords(), actualResult.getKeywords());
        }
    }

    @DisplayName("메뉴 키워드 조회 - 메뉴 이름에 대한 키워드가 10개 미만이고, 장소 카테고리에 대한 키워드까지 포함하여 10개 이상인 경우")
    @Test
    void givenTenPlusKeywordsIncludingKeywordsForPlaceCategories_whenGetKeywords_thenReturnKeywords() {
        // given
        String menuName = "새우버거";
        EnumMap<MenuKeywordCategory, List<String>> namesMap = new EnumMap<>(Map.of(
                MENU_NAME, List.of("갈비", "버거"),
                PLACE_CATEGORY, List.of("햄버거")
        ));
        List<MenuKeyword> menuKeywordsFromNamesForMenuName = List.of(MenuKeyword.of(MENU_NAME, "버거", List.of("1", "2", "3", "4", "5", "6")));
        List<MenuKeyword> menuKeywordsFromNamesForPlaceCategory = List.of(MenuKeyword.of(PLACE_CATEGORY, "햄버거", List.of("7", "8", "9", "10", "11", "12")));
        List<String> filteredNamesForMenuName = List.of("버거");
        List<String> filteredNamesForPlaceCategory = List.of("햄버거");
        List<MenuKeywordResponse> expectedResults = List.of(new MenuKeywordResponse(menuName, List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")));
        given(menuKeywordRepository.getAllByNames(filteredNamesForMenuName)).willReturn(menuKeywordsFromNamesForMenuName);
        given(menuKeywordRepository.getAllByNames(filteredNamesForPlaceCategory)).willReturn(menuKeywordsFromNamesForPlaceCategory);

        // when
        List<MenuKeywordResponse> actualResults = sut.getKeywords(
                new PlaceCategory("양식", "햄버거", null),
                List.of(menuName),
                namesMap,
                List.of("100", "101", "102")
        );

        // then
        then(menuKeywordRepository).should().getAllByNames(filteredNamesForMenuName);
        then(menuKeywordRepository).should().getAllByNames(filteredNamesForPlaceCategory);
        then(menuKeywordRepository).shouldHaveNoMoreInteractions();
        assertThat(actualResults).isNotEmpty();
        assertThat(actualResults.size()).isEqualTo(expectedResults.size());
        for (int i = 0; i < expectedResults.size(); i++) {
            MenuKeywordResponse expectedResult = expectedResults.get(i);
            MenuKeywordResponse actualResult = actualResults.get(i);
            assertThat(actualResult.getMenu()).isEqualTo(expectedResult.getMenu());
            assertIterableEquals(expectedResult.getKeywords(), actualResult.getKeywords());
        }
    }

    @DisplayName("메뉴 키워드 조회 - 메뉴 이름과 장소 카테고리에 대한 키워드가 10개 미만인 경우")
    @Test
    void givenLessThanTenKeywordsForMenuNamesAndPlaceCategories_whenGetKeywords_thenReturnResult() {
        // given
        String menuName = "독도새우 케이크";
        EnumMap<MenuKeywordCategory, List<String>> namesMap = new EnumMap<>(Map.of(
                MENU_NAME, List.of("갈비", "버거", "한우"),
                PLACE_CATEGORY, List.of("양식", "일식")
        ));
        List<String> defaultKeywords = List.of("100", "101", "102");
        List<MenuKeywordResponse> expectedResults = List.of(new MenuKeywordResponse(menuName, defaultKeywords));
        given(menuKeywordRepository.getAllByNames(List.of())).willReturn(List.of());

        // when
        List<MenuKeywordResponse> actualResults = sut.getKeywords(
                new PlaceCategory("카페", "테마카페", "디저트카페"),
                List.of(menuName),
                namesMap,
                defaultKeywords
        );

        // then
        assertThat(actualResults).isNotEmpty();
        assertThat(actualResults.size()).isEqualTo(expectedResults.size());
        for (int i = 0; i < expectedResults.size(); i++) {
            MenuKeywordResponse expectedResult = expectedResults.get(i);
            MenuKeywordResponse actualResult = actualResults.get(i);
            assertThat(actualResult.getMenu()).isEqualTo(expectedResult.getMenu());
            assertIterableEquals(expectedResult.getKeywords(), actualResult.getKeywords());
        }
    }
}