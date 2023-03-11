package com.zelusik.eatery.app.controller;

import com.zelusik.eatery.app.config.SecurityConfig;
import com.zelusik.eatery.app.service.PlaceService;
import com.zelusik.eatery.global.security.JwtAuthenticationFilter;
import com.zelusik.eatery.global.security.UserPrincipal;
import com.zelusik.eatery.util.MemberTestUtils;
import com.zelusik.eatery.util.PlaceTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Controller] Place")
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(
        controllers = PlaceController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
class PlaceControllerTest {

    @MockBean
    PlaceService placeService;

    private final MockMvc mvc;

    public PlaceControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("가게의 id(PK)가 주어지고, 존재하는 장소를 찾는다면, 장소 정보를 반환한다.")
    @Test
    void givenPlaceId_whenFindExistentPlace_thenReturnPlace() throws Exception {
        // given
        long placeId = 1L;
        given(placeService.findDtoById(placeId))
                .willReturn(PlaceTestUtils.createPlaceDtoWithIdAndOpeningHours());

        // when & then
        mvc.perform(
                        get("/api/places/" + placeId)
                                .with(csrf())
                                .with(user(UserPrincipal.of(MemberTestUtils.createMemberDtoWithId())))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(placeId));
    }
}