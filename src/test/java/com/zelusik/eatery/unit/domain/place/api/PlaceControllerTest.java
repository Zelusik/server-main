package com.zelusik.eatery.unit.domain.place.api;

import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.opening_hour.dto.OpeningHourDto;
import com.zelusik.eatery.domain.place.api.PlaceController;
import com.zelusik.eatery.domain.place.constant.DayOfWeek;
import com.zelusik.eatery.domain.place.constant.FilteringType;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.place.dto.PlaceFilteringKeywordDto;
import com.zelusik.eatery.domain.place.dto.PlaceWithMarkedStatusAndImagesDto;
import com.zelusik.eatery.domain.place.dto.request.FindNearPlacesFilteringConditionRequest;
import com.zelusik.eatery.domain.place.dto.request.PlaceCreateRequest;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.place.service.PlaceCommandService;
import com.zelusik.eatery.domain.place.service.PlaceQueryService;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.global.common.constant.EateryConstants;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import com.zelusik.eatery.global.auth.UserPrincipal;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static com.zelusik.eatery.domain.place.constant.DayOfWeek.*;
import static com.zelusik.eatery.global.common.constant.EateryConstants.API_MINOR_VERSION_HEADER_NAME;
import static org.hamcrest.Matchers.hasSize;
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

@DisplayName("[Unit] Controller - Place")
@MockBean(JpaMetamodelMappingContext.class)
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = PlaceController.class)
class PlaceControllerTest {

    @MockBean
    private PlaceCommandService placeCommandService;
    @MockBean
    private PlaceQueryService placeQueryService;

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
        long memberId = 1L;
        long placeId = 2L;
        PlaceCreateRequest placeCreateRequest = createPlaceRequest();
        given(placeCommandService.create(any(PlaceCreateRequest.class))).willReturn(createPlaceDto(placeId, "123"));

        // when & then
        mvc.perform(
                        post("/api/v1/places")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(placeCreateRequest))
                                .with(user(createTestUserDetails(memberId)))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andDo(print());
        then(placeCommandService).should().create(any(PlaceCreateRequest.class));
        then(placeQueryService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("가게의 id(PK)가 주어지고, 해당하는 장소를 단건 조회하면, 조회된 장소 정보를 반환한다.")
    @Test
    void givenPlaceId_whenFindPlace_thenReturnFoundPlace() throws Exception {
        // given
        long memberId = 1L;
        long placeId = 2L;
        PlaceWithMarkedStatusAndImagesDto expectedResult = createPlaceWithMarkedStatusAndImagesDto(placeId, "123");
        given(placeQueryService.getDtoWithMarkedStatusAndImagesById(memberId, placeId)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/v1/places/" + placeId)
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .with(user(createTestUserDetails(memberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(placeId))
                .andExpect(jsonPath("$.isMarked").isNotEmpty())
                .andExpect(jsonPath("$.placeImages").isArray())
                .andDo(print());
        then(placeQueryService).should().getDtoWithMarkedStatusAndImagesById(memberId, placeId);
        then(placeQueryService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("Kakao place unique id가 주어지고, 해당하는 장소를 단건 조회하면, 조회된 장소 정보를 반환한다.")
    @Test
    void givenKakaoPid_whenFindPlace_thenReturnFoundPlace() throws Exception {
        // given
        long memberId = 1L;
        String kakaoPid = "12345";
        PlaceWithMarkedStatusAndImagesDto expectedResult = createPlaceWithMarkedStatusAndImagesDto(2L, kakaoPid);
        given(placeQueryService.getDtoWithMarkedStatusAndImagesByKakaoPid(memberId, kakaoPid)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/v1/places")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .param("kakaoPid", kakaoPid)
                                .with(user(createTestUserDetails(memberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.isMarked").isNotEmpty())
                .andExpect(jsonPath("$.placeImages").isArray())
                .andDo(print());
        then(placeQueryService).should().getDtoWithMarkedStatusAndImagesByKakaoPid(memberId, kakaoPid);
        then(placeQueryService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("검색 키워드로 장소를 검색하면, 조회된 장소들이 반환된다.")
    @Test
    void givenSearchKeyword_whenSearching_thenReturnSearchedPlaces() throws Exception {
        // given
        String searchKeyword = "강남";
        long placeId = 2L;
        Slice<PlaceDto> expectedResult = new SliceImpl<>(List.of(createPlaceDto(placeId, "123")));
        given(placeQueryService.searchDtosByKeyword(eq(searchKeyword), any(Pageable.class))).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/v1/places/search")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .queryParam("keyword", searchKeyword)
                                .with(user(createTestUserDetails(1L)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents", hasSize(expectedResult.getContent().size())))
                .andExpect(jsonPath("$.contents[0].id").value(placeId))
                .andDo(print());
        then(placeQueryService).should().searchDtosByKeyword(eq(searchKeyword), any(Pageable.class));
        then(placeQueryService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("중심 좌표가 주어지고, 근처 장소들을 검색하면, 검색된 장소들을 응답한다.")
    @Test
    void givenCenterPoint_whenFindNearPlaces_thenReturnPlaces() throws Exception {
        // given
        long memberId = 1L;
        long placeId = 2L;
        Point point = new Point("37", "127");
        FindNearPlacesFilteringConditionRequest filteringCondition = new FindNearPlacesFilteringConditionRequest(
                FoodCategoryValue.KOREAN,
                List.of(MON, WED, FRI),
                ReviewKeywordValue.WITH_ALCOHOL,
                false
        );
        PageImpl<PlaceWithMarkedStatusAndImagesDto> expectedResult = new PageImpl<>(List.of(createPlaceWithMarkedStatusAndImagesDto(placeId, "123")), Pageable.ofSize(30), 1);
        given(placeQueryService.findDtosWithoutOpeningHoursNearBy(eq(memberId), any(FindNearPlacesFilteringConditionRequest.class), eq(point), any(Pageable.class))).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/v1/places/near")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .queryParam("lat", point.getLat())
                                .queryParam("lng", point.getLng())
                                .queryParam("foodCategory", filteringCondition.getFoodCategory().name())
                                .queryParam("daysOfWeek", MON.name(), WED.name(), FRI.name())
                                .queryParam("preferredVibe", filteringCondition.getPreferredVibe().name())
                                .with(user(createTestUserDetails(memberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isEmpty").value(false))
                .andExpect(jsonPath("$.numOfElements").value(1))
                .andDo(print());
        then(placeQueryService).should().findDtosWithoutOpeningHoursNearBy(eq(memberId), any(FindNearPlacesFilteringConditionRequest.class), eq(point), any(Pageable.class));
        then(placeQueryService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("선호하는 분위기 필드에 분위기 유형이 아닌 값이 주어지고, 근처 장소들을 검색하면, 예외가 발생한다.")
    @Test
    void givenPreferredVibeNotVibeType_whenFindNearPlaces_thenThrowInvalidTypeOfReviewKeywordValueException() throws Exception {
        // given
        Point point = new Point("37", "127");
        ReviewKeywordValue preferredVibe = ReviewKeywordValue.FRESH;

        // when & then
        mvc.perform(
                        get("/api/v1/places/near")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .queryParam("lat", point.getLat())
                                .queryParam("lng", point.getLng())
                                .queryParam("preferredVibe", preferredVibe.name())
                                .with(user(createTestUserDetails(1L)))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
        then(placeQueryService).shouldHaveNoInteractions();
    }

    @DisplayName("내가 저장한 장소들에 대한 filtering keyword를 조회하면, 조회 결과가 응답된다.")
    @Test
    void given_whenGetFilteringKeywords_thenReturnFilteringKeywords() throws Exception {
        // given
        long memberId = 1L;
        given(placeQueryService.getFilteringKeywords(memberId))
                .willReturn(List.of(
                        new PlaceFilteringKeywordDto("연남동", 5, FilteringType.ADDRESS),
                        new PlaceFilteringKeywordDto("신선한 재료", 3, FilteringType.TOP_3_KEYWORDS)
                ));

        // when & then
        mvc.perform(
                        get("/api/v1/places/bookmarks/filtering-keywords")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .with(user(createTestUserDetails(memberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keywords").isArray())
                .andDo(print());
        then(placeQueryService).should().getFilteringKeywords(memberId);
        then(placeQueryService).shouldHaveNoMoreInteractions();
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
        PageImpl<PlaceWithMarkedStatusAndImagesDto> expectedResult = new PageImpl<>(List.of(createPlaceWithMarkedStatusAndImagesDto(placeId, "123")));
        given(placeQueryService.findMarkedDtosWithoutOpeningHours(eq(memberId), eq(filteringType), eq(filteringKeyword), any(Pageable.class))).willReturn(expectedResult);

        // when & then
        mvc.perform(get("/api/v1/places/bookmarks")
                        .header(API_MINOR_VERSION_HEADER_NAME, 1)
                        .queryParam("type", filteringType.toString())
                        .queryParam("keyword", filteringKeywordDescription)
                        .with(user(createTestUserDetails(memberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents").isArray())
                .andExpect(jsonPath("$.size").value(1))
                .andDo(print());
        then(placeQueryService).should().findMarkedDtosWithoutOpeningHours(eq(memberId), eq(filteringType), eq(filteringKeyword), any(Pageable.class));
        then(placeQueryService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("kakao place id가 주어지고, 주어진 kakao place id로 장소의 DB 존재 여부를 조회하면, 조회된 결과를 반환한다.")
    @Test
    void givenKakaoPid_whenGetExistenceOfPlaceByKakaoPid_thenReturnExistenceOfPlace() throws Exception {
        // given
        String kakaoPid = "123";
        boolean expectedResult = true;
        given(placeQueryService.existsByKakaoPid(kakaoPid)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/v1/places/existence")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .queryParam("kakaoPid", kakaoPid)
                                .with(user(createTestUserDetails(1L)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.existenceOfPlace").value(expectedResult));
        then(placeQueryService).should().existsByKakaoPid(kakaoPid);
        then(placeQueryService).shouldHaveNoMoreInteractions();
    }

    private PlaceCreateRequest createPlaceRequest() {
        return PlaceCreateRequest.of(
                "308342289",
                "연남토마 본점",
                "http://place.map.kakao.com/308342289",
                KakaoCategoryGroupCode.FD6,
                "음식점 > 퓨전요리 > 퓨전일식",
                "02-332-8064",
                "서울 마포구 연남동 568-26",
                "서울 마포구 월드컵북로6길 61",
                "37.5595073462493",
                "126.921462488105"
        );
    }

    private PlaceDto createPlaceDto(Long placeId, String kakaoPid) {
        return new PlaceDto(
                placeId,
                List.of(ReviewKeywordValue.FRESH),
                kakaoPid,
                "연남토마 본점",
                "http://place.map.kakao.com/308342289",
                KakaoCategoryGroupCode.FD6,
                PlaceCategory.of("음식점 > 퓨전요리 > 퓨전일식"),
                "02-332-8064",
                Address.of("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"),
                "http://place.map.kakao.com/308342289",
                new Point("37.5595073462493", "126.921462488105"),
                null,
                List.of(
                        createOpeningHourDto(1L, DayOfWeek.MON, LocalTime.of(12, 0), LocalTime.of(18, 0)),
                        createOpeningHourDto(1L, DayOfWeek.TUE, LocalTime.of(12, 0), LocalTime.of(18, 0)),
                        createOpeningHourDto(1L, DayOfWeek.WED, LocalTime.of(12, 0), LocalTime.of(18, 0))
                )
        );
    }

    private PlaceWithMarkedStatusAndImagesDto createPlaceWithMarkedStatusAndImagesDto(long placeId, String kakaoPid) {
        return new PlaceWithMarkedStatusAndImagesDto(
                placeId,
                List.of(ReviewKeywordValue.FRESH),
                kakaoPid,
                "연남토마 본점",
                "https://place.map.kakao.com/308342289",
                KakaoCategoryGroupCode.FD6,
                PlaceCategory.of("음식점 > 퓨전요리 > 퓨전일식"),
                "02-332-8064",
                Address.of("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"),
                "https://place.map.kakao.com/308342289",
                new Point("37.5595073462493", "126.921462488105"),
                null,
                List.of(),
                false,
                List.of()
        );
    }

    private OpeningHourDto createOpeningHourDto(Long id, DayOfWeek dayOfWeek, LocalTime openAt, LocalTime closeAt) {
        return OpeningHourDto.of(id, 1L, dayOfWeek, openAt, closeAt);
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