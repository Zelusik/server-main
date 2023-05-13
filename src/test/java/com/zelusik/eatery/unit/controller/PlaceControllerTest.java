package com.zelusik.eatery.unit.controller;

import com.zelusik.eatery.config.SecurityConfig;
import com.zelusik.eatery.constant.place.FilteringType;
import com.zelusik.eatery.constant.place.PlaceSearchKeyword;
import com.zelusik.eatery.controller.PlaceController;
import com.zelusik.eatery.dto.place.PlaceDtoWithImages;
import com.zelusik.eatery.dto.place.PlaceFilteringKeywordDto;
import com.zelusik.eatery.dto.place.request.PlaceCreateRequest;
import com.zelusik.eatery.security.JwtAuthenticationFilter;
import com.zelusik.eatery.security.UserPrincipal;
import com.zelusik.eatery.service.PlaceService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static com.zelusik.eatery.constant.place.DayOfWeek.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private final ObjectMapper mapper;

    public PlaceControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
        this.mapper = new ObjectMapper();
    }

    @DisplayName("장소 정보가 주어지면, 장소를 저장한다.")
    @Test
    void givenPlaceInfo_whenSaving_thenSavePlace() throws Exception {
        // given
        PlaceCreateRequest placeCreateRequest = PlaceTestUtils.createPlaceRequest();
        given(placeService.createAndReturnDto(eq(1L), any(PlaceCreateRequest.class)))
                .willReturn(PlaceTestUtils.createPlaceDtoWithOpeningHours());

        // when & then
        mvc.perform(
                        post("/api/places")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(placeCreateRequest))
                                .with(csrf())
                                .with(user(UserPrincipal.of(MemberTestUtils.createMemberDtoWithId())))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @DisplayName("가게의 id(PK)가 주어지고, 존재하는 장소를 찾는다면, 장소 정보를 반환한다.")
    @Test
    void givenPlaceId_whenFindExistentPlace_thenReturnPlace() throws Exception {
        // given
        long placeId = 1L;
        long memberId = 1L;
        given(placeService.findDtoById(memberId, placeId))
                .willReturn(PlaceTestUtils.createPlaceDtoWithImagesAndOpeningHours());

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
        SliceImpl<PlaceDtoWithImages> expectedResult = new SliceImpl<>(List.of(PlaceTestUtils.createPlaceDtoWithImagesAndOpeningHours()), pageable, false);
        given(placeService.findDtosNearBy(1L, List.of(MON, WED, FRI), PlaceSearchKeyword.ALONE, lat, lng, pageable)).willReturn(expectedResult);

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

    @DisplayName("내가 저장한 장소들에 대한 filtering keyword를 조회하면, 조회 결과가 응답된다.")
    @Test
    void given_whenGetFilteringKeywords_thenReturnFilteringKeywords() throws Exception {
        // given
        long memberId = 1L;
        given(placeService.getFilteringKeywords(memberId))
                .willReturn(List.of(
                        PlaceFilteringKeywordDto.of("연남동", 5, FilteringType.ADDRESS),
                        PlaceFilteringKeywordDto.of("신선한 재료", 3, FilteringType.TOP_3_KEYWORDS)
                ));

        // when & then
        mvc.perform(
                        get("/api/places/bookmarks/filtering-keywords")
                                .with(csrf())
                                .with(user(UserPrincipal.of(MemberTestUtils.createMemberDtoWithId(memberId))))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keywords").isArray());
    }

    @DisplayName("필터링 조건이 주어지고, 북마크에 저장한 장소들을 조회하면, 저장된 장소들이 반환된다.")
    @Test
    void givenFilteringConditions_whenFindingMarkedPlaces_thenReturnMarkedPlaces() throws Exception {
        // given
        long memberId = 1L;
        long placeId = 2L;
        FilteringType filteringType = FilteringType.TOP_3_KEYWORDS;
        String filteringKeywordDescription = "신선한 재료";
        String filteringKeyword = "FRESH";
        SliceImpl<PlaceDtoWithImages> expectedResult = new SliceImpl<>(List.of(PlaceTestUtils.createPlaceDtoWithImagesAndOpeningHours(placeId)));
        given(placeService.findMarkedDtos(eq(memberId), eq(filteringType), eq(filteringKeyword), any(Pageable.class)))
                .willReturn(expectedResult);

        // when & then
        mvc.perform(get("/api/places/bookmarks")
                        .queryParam("type", filteringType.toString())
                        .queryParam("keyword", filteringKeywordDescription)
                        .with(csrf())
                        .with(user(UserPrincipal.of(MemberTestUtils.createMemberDtoWithId(memberId))))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents").isArray())
                .andExpect(jsonPath("$.size").value(1));
        then(placeService).should().findMarkedDtos(eq(memberId), eq(filteringType), eq(filteringKeyword), any(Pageable.class));
        then(placeService).shouldHaveNoMoreInteractions();
    }
}