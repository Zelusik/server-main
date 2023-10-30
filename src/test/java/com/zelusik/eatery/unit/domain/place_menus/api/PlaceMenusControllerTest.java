package com.zelusik.eatery.unit.domain.place_menus.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.place_menus.api.PlaceMenusController;
import com.zelusik.eatery.domain.place_menus.dto.PlaceMenusDto;
import com.zelusik.eatery.domain.place_menus.dto.request.AddMenuToPlaceMenusRequest;
import com.zelusik.eatery.domain.place_menus.dto.request.PlaceMenusUpdateRequest;
import com.zelusik.eatery.domain.place_menus.service.PlaceMenusCommandService;
import com.zelusik.eatery.domain.place_menus.service.PlaceMenusQueryService;
import com.zelusik.eatery.global.common.constant.EateryConstants;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import com.zelusik.eatery.global.config.JpaConfig;
import com.zelusik.eatery.global.auth.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.zelusik.eatery.global.common.constant.EateryConstants.API_MINOR_VERSION_HEADER_NAME;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("[Unit] Controller - Place menus")
@MockBean(JpaMetamodelMappingContext.class)
@Import({TestSecurityConfig.class, JpaConfig.class})
@WebMvcTest(controllers = PlaceMenusController.class)
class PlaceMenusControllerTest {

    @MockBean
    private PlaceMenusCommandService placeMenusCommandService;
    @MockBean
    private PlaceMenusQueryService placeMenusQueryService;

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @Autowired
    public PlaceMenusControllerTest(MockMvc mvc, ObjectMapper mapper) {
        this.mvc = mvc;
        this.mapper = mapper;
    }

    @DisplayName("장소 PK가 주어지고, 장소 메뉴 목록을 저장하려고 하면, 해당 장소에 대한 메뉴 목록이 저장된다.")
    @Test
    void givenPlaceId_whenSavePlaceMenus_thenReturnSavedPlaceMenus() throws Exception {
        // given
        long loginMemberId = 3L;
        long placeId = 1L;
        long placeMenusId = 2L;
        List<String> menus = List.of("돈까스", "계란찜", "라면");
        PlaceMenusDto expectedResult = createPlaceMenusDto(placeMenusId, placeId, menus);
        given(placeMenusCommandService.savePlaceMenus(placeId)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        post("/api/v1/places/" + placeId + "/menus")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/places/" + placeId + "/menus"))
                .andExpect(jsonPath("$.id").value(placeMenusId))
                .andExpect(jsonPath("$.placeId").value(placeId))
                .andExpect(jsonPath("$.menus").isArray())
                .andExpect(jsonPath("$.menus", hasSize(3)));
    }

    @DisplayName("장소의 PK 값이 주어지고, 장소 메뉴 목록을 조회하면, 조회된 메뉴 목록이 반환된다.")
    @Test
    void givenPlaceId_whenFindPlaceMenus_thenReturnPlaceMenus() throws Exception {
        // given
        long loginMemberId = 3L;
        long placeId = 1L;
        long placeMenusId = 2L;
        List<String> menus = List.of("돈까스", "계란찜", "라면");
        PlaceMenusDto expectedResult = createPlaceMenusDto(placeMenusId, placeId, menus);
        given(placeMenusQueryService.findDtoByPlaceId(placeId)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/v1/places/" + placeId + "/menus")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.placeId").doesNotExist())
                .andExpect(jsonPath("$.menus").isArray())
                .andExpect(jsonPath("$.menus", hasSize(3)));
    }

    @DisplayName("장소의 고유 id값(kakaoPid)이 주어지고, 장소 메뉴 목록을 조회하면, 조회된 메뉴 목록이 반환된다.")
    @Test
    void givenKakaoPid_whenFindPlaceMenus_thenReturnPlaceMenus() throws Exception {
        // given
        long loginMemberId = 3L;
        String kakaoPid = "12345";
        List<String> menus = List.of("돈까스", "계란찜", "라면");
        PlaceMenusDto expectedResult = createPlaceMenusDto(2L, 1L, menus);
        given(placeMenusQueryService.findDtoByKakaoPid(kakaoPid)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/v1/places/menus")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .param("kakaoPid", kakaoPid)
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.placeId").doesNotExist())
                .andExpect(jsonPath("$.menus").isArray())
                .andExpect(jsonPath("$.menus", hasSize(3)));
    }

    @DisplayName("메뉴 목록이 장소의 PK 값과 함께 주어지고, 메뉴 목록을 업데이트하면, 업데이트된 메뉴 목록 정보가 반환된다.")
    @Test
    void givenMenusWithPlaceId_whenUpdateMenus_thenReturnUpdatedPlaceMenus() throws Exception {
        // given
        long loginMemberId = 3L;
        long placeId = 1L;
        long placeMenusId = 2L;
        List<String> menusForUpdate = List.of("치킨");
        PlaceMenusUpdateRequest requestBody = new PlaceMenusUpdateRequest(menusForUpdate);
        PlaceMenusDto expectedResult = createPlaceMenusDto(placeMenusId, placeId, menusForUpdate);
        given(placeMenusCommandService.updateMenus(placeId, menusForUpdate)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        put("/api/v1/places/" + placeId + "/menus")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .content(mapper.writeValueAsString(requestBody))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(placeMenusId))
                .andExpect(jsonPath("$.placeId").value(placeId))
                .andExpect(jsonPath("$.menus").isArray())
                .andExpect(jsonPath("$.menus", hasSize(1)));
    }

    @DisplayName("메뉴와 장소의 PK 값이 주어지고, 기존 메뉴 목록에 전달받은 메뉴를 새로 추가하면, 업데이트된 메뉴 목록이 반환된다.")
    @Test
    void givenMenuWithPlaceId_whenAddMenu_thenReturnUpdatedPlaceMenus() throws Exception {
        // given
        long loginMemberId = 3L;
        long placeId = 1L;
        long placeMenusId = 2L;
        String menuForAdd = "양념치킨";
        AddMenuToPlaceMenusRequest requestBody = new AddMenuToPlaceMenusRequest(menuForAdd);
        PlaceMenusDto expectedResult = createPlaceMenusDto(placeMenusId, placeId, List.of("후라이드치킨", menuForAdd));
        given(placeMenusCommandService.addMenu(placeId, menuForAdd)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        patch("/api/v1/places/" + placeId + "/menus")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .content(mapper.writeValueAsString(requestBody))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(placeMenusId))
                .andExpect(jsonPath("$.placeId").value(placeId))
                .andExpect(jsonPath("$.menus").isArray())
                .andExpect(jsonPath("$.menus", hasSize(2)));
    }

    @DisplayName("장소의 PK 값이 주어지고, 해당하는 장소의 메뉴 목록을 삭제한다.")
    @Test
    void givenPlaceIdWithAdmin_whenDeletePlaceMenus_thenDeleting() throws Exception {
        // given
        long loginMemberId = 2L;
        long placeId = 1L;
        willDoNothing().given(placeMenusCommandService).delete(placeId);

        // when & then
        mvc.perform(
                        delete("/api/v1/places/" + placeId + "/menus")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .with(user(createTestAdminDetails(loginMemberId)))
                )
                .andExpect(status().isOk());
    }

    @DisplayName("장소의 PK 값이 주어지고, 일반 사용자가 해당하는 장소의 메뉴 목록을 삭제하면, 접근이 거부된다.")
    @Test
    void givenPlaceIdWithUser_whenDeletePlaceMenus_thenAccessDenied() throws Exception {
        // given
        long loginMemberId = 2L;
        long placeId = 1L;
        willDoNothing().given(placeMenusCommandService).delete(placeId);

        // when & then
        mvc.perform(
                        delete("/api/v1/places/" + placeId + "/menus")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isForbidden());
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

    private UserDetails createTestUserDetails(long loginMemberId) {
        return UserPrincipal.of(createMemberDto(loginMemberId, Set.of(RoleType.USER)));
    }

    private UserDetails createTestAdminDetails(long loginMemberId) {
        return UserPrincipal.of(createMemberDto(loginMemberId, Set.of(RoleType.USER, RoleType.MANAGER, RoleType.ADMIN)));
    }

    private PlaceMenusDto createPlaceMenusDto(Long id, Long placeId, List<String> menus) {
        return PlaceMenusDto.of(id, placeId, menus);
    }
}