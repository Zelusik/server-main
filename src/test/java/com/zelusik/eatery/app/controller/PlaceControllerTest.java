package com.zelusik.eatery.app.controller;

import com.zelusik.eatery.app.config.SecurityConfig;
import com.zelusik.eatery.app.constant.place.DayOfWeek;
import com.zelusik.eatery.app.constant.place.PlaceSearchKeyword;
import com.zelusik.eatery.app.dto.place.PlaceDto;
import com.zelusik.eatery.app.dto.place.response.PlaceResponse;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.zelusik.eatery.app.constant.place.DayOfWeek.*;
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

    @DisplayName("중심 좌표가 주어지고, 근처 장소들을 검색하면, 검색된 장소들을 응답한다.")
    @Test
    void givenCenterPoint_whenSearchNearByPlaces_thenReturnPlaces() throws Exception {
        // given
        String lat = "37";
        String lng = "127";
        Pageable pageable = Pageable.ofSize(30);
        SliceImpl<PlaceDto> expectedResult = new SliceImpl<>(List.of(PlaceTestUtils.createPlaceDtoWithIdAndOpeningHours()), pageable, false);
        given(placeService.findDtosNearBy(List.of(MON, WED, FRI), PlaceSearchKeyword.ALONE, lat, lng, pageable)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/places/search?lat=" + lat + "&lng=" + lng + "&daysOfWeek=월,수,금" + "&keyword=혼밥")
                                .with(csrf())
                                .with(user(UserPrincipal.of(MemberTestUtils.createMemberDtoWithId())))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasContent").value(true))
                .andExpect(jsonPath("$.numOfElements").value(1));
    }
}