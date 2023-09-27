package com.zelusik.eatery.integration.repository.menu_keyword;

import com.zelusik.eatery.global.config.JpaConfig;
import com.zelusik.eatery.global.config.QuerydslConfig;
import com.zelusik.eatery.domain.menu_keyword.entity.MenuKeyword;
import com.zelusik.eatery.domain.menu_keyword.repository.MenuKeywordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.zelusik.eatery.domain.review_keyword.constant.MenuKeywordCategory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@DisplayName("[Integration] MenuKeyword Repository")
@Import({QuerydslConfig.class, JpaConfig.class})
@ActiveProfiles("test")
@DataJpaTest
class MenuKeywordRepositoryTest {

    private final MenuKeywordRepository sut;

    public MenuKeywordRepositoryTest(@Autowired MenuKeywordRepository sut) {
        this.sut = sut;
    }

    @BeforeEach
    void createTestData() {
        sut.save(MenuKeyword.of(DEFAULT, "", List.of("다양한", "상큼한", "짭조름한", "달콤한", "매콤한", "독특한")));
        sut.save(MenuKeyword.of(PLACE_CATEGORY, "일본식라면", List.of("강렬한", "탱글탱글한", "쫄깃한", "매콤한", "깊은", "다채로운", "조화로운")));
        sut.save(MenuKeyword.of(PLACE_CATEGORY, "육류,고기", List.of("살살 녹는", "탱글탱글한", "짭조름한", "겉바속촉", "풍미 있는", "새콤달콤한", "깊은")));
        sut.save(MenuKeyword.of(PLACE_CATEGORY, "햄버거", List.of("신선한", "바삭한", "부드러운", "가벼운", "감칠맛 나는", "짭짤한", "건강한")));
        sut.save(MenuKeyword.of(PLACE_CATEGORY, "일식", List.of("신선한", "깔끔한", "감칠맛 나는", "독특한", "다양한", "고급진")));
        sut.save(MenuKeyword.of(PLACE_CATEGORY, "중국요리", List.of("짭쪼름한", "독특한", "불맛이 나는", "깊은", "달콤한", "풍부한", "다양한")));
        sut.save(MenuKeyword.of(MENU_NAME, "곰탕", List.of("진한", "고소한", "푸짐한", "건강에 좋은", "부드러운", "담백한", "깊은", "고기가 가득한", "시원한")));
        sut.save(MenuKeyword.of(MENU_NAME, "국밥", List.of("진한", "고소한", "푸짐한", "건강에 좋은", "부드러운", "짭짤한", "담백한", "깊은", "고기가 가득한", "시원한")));
        sut.save(MenuKeyword.of(MENU_NAME, "국수", List.of("쫄깃한", "매콤한", "감칠맛 나는", "부드러운", "시원한", "짭조름한", "담백한", "입맛이 돋는", "상큼한", "고소한")));
        sut.save(MenuKeyword.of(MENU_NAME, "냉면", List.of("쫄깃한", "매콤한", "감칠맛 나는", "시원한", "짭조름한", "담백한", "입맛이 돋는", "상큼한")));
        sut.save(MenuKeyword.of(MENU_NAME, "닭강정", List.of("바삭한", "달콤한", "매콤한", "담백한", "고소한", "육즙이 살아있는")));
        sut.save(MenuKeyword.of(MENU_NAME, "도넛", List.of("달콤한", "바삭한", "촉촉한", "고급스러운", "독특한")));
        sut.save(MenuKeyword.of(MENU_NAME, "까스", List.of("바삭한", "촉촉한", "입에서 살살 녹는", "담백한", "푸짐한", "매콤한")));
        sut.save(MenuKeyword.of(MENU_NAME, "우동", List.of("쫄깃한", "깊은 맛", "담백한", "오동통한", "상큼한", "뜨끈한")));
        sut.save(MenuKeyword.of(MENU_NAME, "두부", List.of("고소한", "야들야들한")));
        sut.save(MenuKeyword.of(MENU_NAME, "버거", List.of("신선한", "바삭한", "부드러운", "가벼운", "감칠맛 나는", "짭짤한", "건강한")));
    }

    @DisplayName("Default menu keyword를 조회한다.")
    @Test
    void given_whenGetDefaultMenuKeyword_thenReturnResult() {
        // given

        // when
        Optional<MenuKeyword> result = sut.getDefaultMenuKeyword();

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get())
                .hasFieldOrPropertyWithValue("category", DEFAULT)
                .hasFieldOrPropertyWithValue("name", "")
                .hasFieldOrPropertyWithValue("keywords", List.of("다양한", "상큼한", "짭조름한", "달콤한", "매콤한", "독특한"));
    }

    @DisplayName("카테고리가 MENU_NAME인 메뉴 키워드의 name list를 조회한다.")
    @Test
    void given_whenGetNamesWithCategoryIsMENU_NAME_thenReturnResult() {
        // given
        List<String> expectedResult = List.of("곰탕", "국밥", "국수", "냉면", "닭강정", "도넛", "까스", "우동", "두부", "버거");

        // when
        List<String> actualResult = sut.getNamesByCategory(MENU_NAME);

        // then
        assertThat(actualResult).isNotEmpty();
        assertIterableEquals(expectedResult, actualResult);
    }

    @DisplayName("카테고리가 PLACE_CATEGORY인 메뉴 키워드의 name list를 조회한다.")
    @Test
    void given_whenGetNamesWithCategoryIsPLACE_CATEGORY_thenReturnResult() {
        // given
        List<String> expectedResult = List.of("일본식라면", "육류,고기", "햄버거", "일식", "중국요리");

        // when
        List<String> actualResult = sut.getNamesByCategory(PLACE_CATEGORY);

        // then
        assertThat(actualResult).isNotEmpty();
        assertIterableEquals(expectedResult, actualResult);
    }

    @DisplayName("Name 목록이 주어지고, 주어진 이름들에 해당하는 메뉴 키워드 목록을 조회하면, 조회된 결과를 반환한다.")
    @Test
    void givenNames_whenGetMenuKeywordsMatchingNames_thenReturnResult() {
        // given
        List<String> names = List.of("햄버거", "일식", "두부");
        List<MenuKeyword> expectedResults = new ArrayList<>(List.of(
                MenuKeyword.of(PLACE_CATEGORY, "햄버거", List.of("신선한", "바삭한", "부드러운", "가벼운", "감칠맛 나는", "짭짤한", "건강한")),
                MenuKeyword.of(PLACE_CATEGORY, "일식", List.of("신선한", "깔끔한", "감칠맛 나는", "독특한", "다양한", "고급진")),
                MenuKeyword.of(MENU_NAME, "두부", List.of("고소한", "야들야들한"))
        ));

        // when
        List<MenuKeyword> actualResults = sut.getAllByNames(names);

        // then
        assertThat(actualResults).isNotEmpty();
        assertThat(actualResults.size()).isEqualTo(expectedResults.size());
        expectedResults.sort(Comparator.comparing(MenuKeyword::getName));
        actualResults.sort(Comparator.comparing(MenuKeyword::getName));
        for (int i = 0; i < expectedResults.size(); i++) {
            MenuKeyword expectedResult = expectedResults.get(i);
            MenuKeyword actualResult = actualResults.get(i);
            assertThat(actualResult.getCategory()).isEqualTo(expectedResult.getCategory());
            assertThat(actualResult.getName()).isEqualTo(expectedResult.getName());
            assertIterableEquals(expectedResult.getKeywords(), actualResult.getKeywords());
        }
    }
}