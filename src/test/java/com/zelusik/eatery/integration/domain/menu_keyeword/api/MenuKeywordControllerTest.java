package com.zelusik.eatery.integration.domain.menu_keyeword.api;

import com.zelusik.eatery.global.config.JpaConfig;
import com.zelusik.eatery.global.config.QuerydslConfig;
import com.zelusik.eatery.domain.menu_keyword.api.MenuKeywordController;
import com.zelusik.eatery.domain.menu_keyword.entity.MenuKeyword;
import com.zelusik.eatery.domain.menu_keyword.dto.response.MenuKeywordResponse;
import com.zelusik.eatery.domain.menu_keyword.repository.MenuKeywordRepository;
import com.zelusik.eatery.domain.menu_keyword.service.MenuKeywordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.zelusik.eatery.domain.review_keyword.constant.MenuKeywordCategory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@DisplayName("[Integration] Menu keyword service")
@ActiveProfiles("test")
@Import({MenuKeywordController.class, MenuKeywordService.class, QuerydslConfig.class, JpaConfig.class})
@DataJpaTest
class MenuKeywordControllerTest {

    private final MenuKeywordRepository menuKeywordRepository;
    private final MenuKeywordController sut;

    @Autowired
    public MenuKeywordControllerTest(MenuKeywordRepository menuKeywordRepository, MenuKeywordController menuKeywordController) {
        this.menuKeywordRepository = menuKeywordRepository;
        this.sut = menuKeywordController;
    }

    @BeforeEach
    void createTestData() {
        menuKeywordRepository.save(MenuKeyword.of(DEFAULT, "", List.of("다양한", "상큼한", "짭조름한", "달콤한", "매콤한", "독특한")));
        menuKeywordRepository.save(MenuKeyword.of(PLACE_CATEGORY, "일본식라면", List.of("강렬한", "탱글탱글한", "쫄깃한", "매콤한", "깊은", "다채로운", "조화로운")));
        menuKeywordRepository.save(MenuKeyword.of(PLACE_CATEGORY, "육류,고기", List.of("살살 녹는", "탱글탱글한", "짭조름한", "겉바속촉", "풍미 있는", "새콤달콤한", "깊은")));
        menuKeywordRepository.save(MenuKeyword.of(PLACE_CATEGORY, "햄버거", List.of("신박한", "바삭한", "부드러운", "가벼운", "감칠맛 나는", "짭짤한", "건강한")));
        menuKeywordRepository.save(MenuKeyword.of(PLACE_CATEGORY, "일식", List.of("신선한", "깔끔한", "감칠맛 나는", "독특한", "다양한", "고급진")));
        menuKeywordRepository.save(MenuKeyword.of(PLACE_CATEGORY, "중국요리", List.of("짭쪼름한", "독특한", "불맛이 나는", "깊은", "달콤한", "풍부한", "다양한")));
        menuKeywordRepository.save(MenuKeyword.of(MENU_NAME, "곰탕", List.of("진한", "고소한", "푸짐한", "건강에 좋은", "부드러운", "담백한", "깊은", "고기가 가득한", "시원한")));
        menuKeywordRepository.save(MenuKeyword.of(MENU_NAME, "국밥", List.of("진한", "고소한", "푸짐한", "건강에 좋은", "부드러운", "짭짤한", "담백한", "깊은", "고기가 가득한", "시원한")));
        menuKeywordRepository.save(MenuKeyword.of(MENU_NAME, "국수", List.of("쫄깃한", "매콤한", "감칠맛 나는", "부드러운", "시원한", "짭조름한", "담백한", "입맛이 돋는", "상큼한", "고소한")));
        menuKeywordRepository.save(MenuKeyword.of(MENU_NAME, "냉면", List.of("쫄깃한", "매콤한", "감칠맛 나는", "시원한", "짭조름한", "담백한", "입맛이 돋는", "상큼한")));
        menuKeywordRepository.save(MenuKeyword.of(MENU_NAME, "닭강정", List.of("바삭한", "달콤한", "매콤한", "담백한", "고소한", "육즙이 살아있는")));
        menuKeywordRepository.save(MenuKeyword.of(MENU_NAME, "도넛", List.of("달콤한", "바삭한", "촉촉한", "고급스러운", "독특한")));
        menuKeywordRepository.save(MenuKeyword.of(MENU_NAME, "까스", List.of("바삭한", "촉촉한", "입에서 살살 녹는", "담백한", "푸짐한", "매콤한")));
        menuKeywordRepository.save(MenuKeyword.of(MENU_NAME, "우동", List.of("쫄깃한", "깊은 맛", "담백한", "오동통한", "상큼한", "뜨끈한")));
        menuKeywordRepository.save(MenuKeyword.of(MENU_NAME, "두부", List.of("고소한", "야들야들한")));
        menuKeywordRepository.save(MenuKeyword.of(MENU_NAME, "버거", List.of("신선한", "바삭한", "부드러운", "가벼운", "감칠맛 나는", "짭짤한", "건강한")));
    }

    @DisplayName("장소 카테고리와 메뉴 목록이 담긴 요청 데이터가 주어지고, 각 메뉴에 적절한 키워드 목록을 조회하면, 조회된 키워드 목록을 반환한다.")
    @Test
    void givenMenuKeywordGetRequest_whenGetKeywords_thenReturnKeywords() {
        // given
        List<MenuKeywordResponse> expectedResults = List.of(
                new MenuKeywordResponse("까스버거", List.of("바삭한", "촉촉한", "입에서 살살 녹는", "담백한", "푸짐한", "매콤한", "신선한", "부드러운", "가벼운", "감칠맛 나는")),   // 메뉴 이름 "까스", "버거"에 매칭
                new MenuKeywordResponse("버거", List.of("신선한", "바삭한", "부드러운", "가벼운", "감칠맛 나는", "짭짤한", "건강한", "신박한", "다양한", "상큼한")), // 메뉴 이름 "버거", 카테고리 "햄버거"에 매칭, 기본 키워드 포함
                new MenuKeywordResponse("특이한거", List.of("신박한", "바삭한", "부드러운", "가벼운", "감칠맛 나는", "짭짤한", "건강한", "다양한", "상큼한", "짭조름한")) // 카테고리 "햄버거"에 매칭, 기본 키워드 포함
        );

        // when
        List<MenuKeywordResponse> actualResults = sut.getMenuKeywordsV1_1("음식점 > 양식 > 햄버거", List.of("까스버거", "버거", "특이한거")).getMenuKeywords();

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