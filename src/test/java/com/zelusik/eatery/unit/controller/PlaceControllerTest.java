package com.zelusik.eatery.unit.controller;

import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.constant.FoodCategoryValue;
import com.zelusik.eatery.constant.place.DayOfWeek;
import com.zelusik.eatery.constant.place.FilteringType;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.controller.PlaceController;
import com.zelusik.eatery.domain.place.Point;
import com.zelusik.eatery.dto.place.PlaceDto;
import com.zelusik.eatery.dto.place.PlaceFilteringKeywordDto;
import com.zelusik.eatery.dto.place.request.PlaceCreateRequest;
import com.zelusik.eatery.security.UserPrincipal;
import com.zelusik.eatery.service.PlaceService;
import com.zelusik.eatery.util.MemberTestUtils;
import com.zelusik.eatery.util.PlaceTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static com.zelusik.eatery.constant.place.DayOfWeek.*;
import static com.zelusik.eatery.util.PlaceTestUtils.createPlaceDto;
import static com.zelusik.eatery.util.PlaceTestUtils.createPlaceDtoWithMarkedStatusAndImages;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Unit] Place Controller")
@MockBean(JpaMetamodelMappingContext.class)
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = PlaceController.class)
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
        given(placeService.create(eq(1L), any(PlaceCreateRequest.class)))
                .willReturn(PlaceTestUtils.createPlaceDto());

        // when & then
        mvc.perform(
                        post("/api/places")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(placeCreateRequest))
                                .with(user(UserPrincipal.of(MemberTestUtils.createMemberDtoWithId())))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @DisplayName("가게의 id(PK)가 주어지고, 해당하는 장소를 단건 조회하면, 조회된 장소 정보를 반환한다.")
    @Test
    void givenPlaceId_whenFindPlace_thenReturnFoundPlace() throws Exception {
        // given
        long memberId = 1L;
        long placeId = 2L;
        PlaceDto expectedResult = createPlaceDtoWithMarkedStatusAndImages(placeId);
        given(placeService.findDtoWithMarkedStatusAndImagesById(memberId, placeId)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/places/" + placeId)
                                .with(user(createTestUserDetails()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(placeId))
                .andExpect(jsonPath("$.isMarked").isNotEmpty())
                .andExpect(jsonPath("$.images", notNullValue()));
    }

    @DisplayName("Kakao place unique id가 주어지고, 해당하는 장소를 단건 조회하면, 조회된 장소 정보를 반환한다.")
    @Test
    void givenKakaoPid_whenFindPlace_thenReturnFoundPlace() throws Exception {
        // given
        long memberId = 1L;
        String kakaoPid = "12345";
        PlaceDto expectedResult = createPlaceDtoWithMarkedStatusAndImages(2L, kakaoPid);
        given(placeService.findDtoWithMarkedStatusAndImagesByKakaoPid(memberId, kakaoPid)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/places")
                                .param("kakaoPid", kakaoPid)
                                .with(user(createTestUserDetails()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.isMarked").isNotEmpty())
                .andExpect(jsonPath("$.images", notNullValue()));
    }

    @DisplayName("검색 키워드로 장소를 검색하면, 조회된 장소들이 반환된다.")
    @Test
    void givenSearchKeyword_whenSearching_thenReturnSearchedPlaces() throws Exception {
        // given
        String searchKeyword = "강남";
        long placeId = 2L;
        Slice<PlaceDto> expectedResult = new SliceImpl<>(List.of(createPlaceDto(placeId)));
        given(placeService.searchDtosByKeyword(eq(searchKeyword), any(Pageable.class))).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/places/search")
                                .queryParam("keyword", searchKeyword)
                                .with(user(createTestUserDetails()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents", hasSize(expectedResult.getContent().size())))
                .andExpect(jsonPath("$.contents[0].id").value(placeId));
    }

    @DisplayName("중심 좌표가 주어지고, 근처 장소들을 검색하면, 검색된 장소들을 응답한다.")
    @Test
    void givenCenterPoint_whenFindNearPlaces_thenReturnPlaces() throws Exception {
        // given
        Point point = new Point("37", "127");
        FoodCategoryValue foodCategory = FoodCategoryValue.KOREAN;
        List<DayOfWeek> daysOfWeek = List.of(MON, WED, FRI);
        ReviewKeywordValue preferredVibe = ReviewKeywordValue.WITH_ALCOHOL;
        PageImpl<PlaceDto> expectedResult = new PageImpl<>(List.of(createPlaceDtoWithMarkedStatusAndImages()), Pageable.ofSize(30), 1);
        given(placeService.findDtosNearBy(eq(1L), eq(foodCategory), eq(daysOfWeek), eq(preferredVibe), eq(point), any(Pageable.class))).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/places/near")
                                .queryParam("lat", point.getLat())
                                .queryParam("lng", point.getLng())
                                .queryParam("foodCategory", foodCategory.name())
                                .queryParam("daysOfWeek", MON.name(), WED.name(), FRI.name())
                                .queryParam("preferredVibe", preferredVibe.name())
                                .with(user(UserPrincipal.of(MemberTestUtils.createMemberDtoWithId())))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isEmpty").value(false))
                .andExpect(jsonPath("$.numOfElements").value(1))
                .andDo(print());
    }

    @DisplayName("선호하는 분위기 필드에 분위기 유형이 아닌 값이 주어지고, 근처 장소들을 검색하면, 예외가 발생한다.")
    @Test
    void givenPreferredVibeNotVibeType_whenFindNearPlaces_thenThrowInvalidTypeOfReviewKeywordValueException() throws Exception {
        // given
        Point point = new Point("37", "127");
        ReviewKeywordValue preferredVibe = ReviewKeywordValue.FRESH;

        // when & then
        mvc.perform(
                        get("/api/places/near")
                                .queryParam("lat", point.getLat())
                                .queryParam("lng", point.getLng())
                                .queryParam("preferredVibe", preferredVibe.name())
                                .with(user(createTestUserDetails()))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
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
                                .with(user(createTestUserDetails()))
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
        SliceImpl<PlaceDto> expectedResult = new SliceImpl<>(List.of(createPlaceDtoWithMarkedStatusAndImages(placeId)));
        given(placeService.findMarkedDtos(eq(memberId), eq(filteringType), eq(filteringKeyword), any(Pageable.class))).willReturn(expectedResult);

        // when & then
        mvc.perform(get("/api/places/bookmarks")
                        .queryParam("type", filteringType.toString())
                        .queryParam("keyword", filteringKeywordDescription)
                        .with(user(createTestUserDetails()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents").isArray())
                .andExpect(jsonPath("$.size").value(1));
        then(placeService).should().findMarkedDtos(eq(memberId), eq(filteringType), eq(filteringKeyword), any(Pageable.class));
        then(placeService).shouldHaveNoMoreInteractions();
    }

    private UserDetails createTestUserDetails() {
        return UserPrincipal.of(MemberTestUtils.createMemberDtoWithId());
    }
}