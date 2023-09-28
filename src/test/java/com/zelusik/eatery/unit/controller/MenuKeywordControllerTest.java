package com.zelusik.eatery.unit.controller;

import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.global.common.constant.EateryConstants;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.menu_keyword.api.MenuKeywordController;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.global.common.dto.ListDto;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.menu_keyword.dto.response.MenuKeywordResponse;
import com.zelusik.eatery.global.security.UserPrincipal;
import com.zelusik.eatery.domain.menu_keyword.service.MenuKeywordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

import static com.zelusik.eatery.global.common.constant.EateryConstants.API_MINOR_VERSION_HEADER_NAME;
import static com.zelusik.eatery.domain.review_keyword.constant.MenuKeywordCategory.MENU_NAME;
import static com.zelusik.eatery.domain.review_keyword.constant.MenuKeywordCategory.PLACE_CATEGORY;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Unit] Menu keyword controller")
@MockBean(JpaMetamodelMappingContext.class)
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = MenuKeywordController.class)
class MenuKeywordControllerTest {

    @MockBean
    private MenuKeywordService menuKeywordService;

    private final MockMvc mvc;

    @Autowired
    public MenuKeywordControllerTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("리뷰를 작성할 장소의 카테고리 정보와 키워드를 얻고자 하는 메뉴 목록이 주어지고, 메뉴에 대한 키워드를 조회하면, 조회된 키워드들을 반환한다.")
    @Test
    void givenPlaceCategoriesAndMenus_whenGetMenuKeywords_thenReturnKeywords() throws Exception {
        // given
        PlaceCategory placeCategory = new PlaceCategory("한식", "육류,고기", null);
        List<String> menus = List.of("왕 생갈비", "한우 양념 갈비");
        List<MenuKeywordResponse> expectedResults = List.of(
                new MenuKeywordResponse(menus.get(0), List.of("감칠맛 나는")),
                new MenuKeywordResponse(menus.get(1), List.of("매운", "퍽퍽한"))
        );
        given(menuKeywordService.getNamesForCategory(MENU_NAME)).willReturn(new ListDto<>(List.of()));
        given(menuKeywordService.getNamesForCategory(PLACE_CATEGORY)).willReturn(new ListDto<>(List.of()));
        given(menuKeywordService.getDefaultKeywords()).willReturn(new ListDto<>(List.of()));
        given(menuKeywordService.getKeywords(eq(placeCategory), eq(menus), any(EnumMap.class), anyList())).willReturn(expectedResults);

        // when & then
        ResultActions resultActions = mvc.perform(
                        get("/api/v1/menu-keywords")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .param("placeCategory", "음식점 > " + placeCategory.getFirstCategory() + " > " + placeCategory.getSecondCategory())
                                .param("menus", menus.toArray(new String[0]))
                                .with(user(createTestUserDetails(1L)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menuKeywords").isArray())
                .andExpect(jsonPath("$.menuKeywords", hasSize(menus.size())));
        for (int i = 0; i < expectedResults.size(); i++) {
            MenuKeywordResponse expectedResult = expectedResults.get(i);
            resultActions
                    .andExpect(jsonPath("$.menuKeywords[" + i + "].menu").value(expectedResult.getMenu()))
                    .andExpect(jsonPath("$.menuKeywords[" + i + "].keywords", hasSize(expectedResult.getKeywords().size())));
        }
        resultActions.andDo(print());
    }

    private MemberDto createMemberDto(Long memberId, Set<RoleType> roleTypes) {
        return new MemberDto(
                memberId,
                EateryConstants.defaultProfileImageUrl,
                EateryConstants.defaultProfileThumbnailImageUrl,
                "1234567890",
                LoginType.KAKAO,
                roleTypes,
                "test@test.com",
                "test",
                LocalDate.of(2000, 1, 1),
                20,
                Gender.MALE,
                List.of(FoodCategoryValue.KOREAN),
                null
        );
    }

    private UserDetails createTestUserDetails(Long loginMemberId, Set<RoleType> roleTypes) {
        return UserPrincipal.of(createMemberDto(loginMemberId, roleTypes));
    }

    private UserDetails createTestUserDetails(Long loginMemberId) {
        return createTestUserDetails(loginMemberId, Set.of(RoleType.USER));
    }
}