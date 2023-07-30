package com.zelusik.eatery.unit.controller;

import com.zelusik.eatery.config.JpaConfig;
import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.controller.PlaceMenusController;
import com.zelusik.eatery.dto.place.PlaceMenusDto;
import com.zelusik.eatery.security.UserPrincipal;
import com.zelusik.eatery.service.PlaceMenusService;
import com.zelusik.eatery.util.MemberTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.zelusik.eatery.util.PlaceTestUtils.createPlaceMenusDto;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("[Unit] Place Menus Controller")
@MockBean(JpaMetamodelMappingContext.class)
@Import({TestSecurityConfig.class, JpaConfig.class})
@WebMvcTest(controllers = PlaceMenusController.class)
class PlaceMenusControllerTest {

    @MockBean
    private PlaceMenusService placeMenusService;

    private final MockMvc mvc;

    @Autowired
    public PlaceMenusControllerTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("장소 PK가 주어지고, 장소 메뉴 목록을 저장하려고 하면, 해당 장소에 대한 메뉴 목록이 저장된다.")
    @Test
    void givenPlaceId_whenSavePlaceMenus_thenReturnSavedPlaceMenus() throws Exception {
        // given
        long placeId = 1L;
        long placeMenusId = 2L;
        List<String> menus = List.of("돈까스", "계란찜", "라면");
        PlaceMenusDto expectedResult = createPlaceMenusDto(placeMenusId, placeId, menus);
        given(placeMenusService.savePlaceMenus(placeId)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        post("/api/places/" + placeId + "/menus")
                                .with(user(createTestUserDetails()))
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/places/" + placeId + "/menus"))
                .andExpect(jsonPath("$.id").value(placeMenusId))
                .andExpect(jsonPath("$.placeId").value(placeId))
                .andExpect(jsonPath("$.menus").isArray())
                .andExpect(jsonPath("$.menus", hasSize(3)));
    }

    private UserDetails createTestUserDetails() {
        return UserPrincipal.of(MemberTestUtils.createMemberDtoWithId());
    }
}