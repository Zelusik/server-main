package com.zelusik.eatery.unit.domain.meeting_place.api;

import com.zelusik.eatery.config.TestSecurityConfig;
import com.zelusik.eatery.domain.location.dto.LocationDto;
import com.zelusik.eatery.domain.location.service.LocationQueryService;
import com.zelusik.eatery.domain.meeting_place.api.MeetingPlaceController;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.global.common.constant.EateryConstants;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import com.zelusik.eatery.global.kakao.dto.KakaoPlaceInfo;
import com.zelusik.eatery.global.kakao.service.KakaoService;
import com.zelusik.eatery.global.auth.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.zelusik.eatery.domain.meeting_place.api.MeetingPlaceController.PAGE_SIZE_OF_SEARCHING_MEETING_PLACES;
import static com.zelusik.eatery.global.common.constant.EateryConstants.API_MINOR_VERSION_HEADER_NAME;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Unit] Controller - Meeting place")
@MockBean(JpaMetamodelMappingContext.class)
@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = MeetingPlaceController.class)
class MeetingPlaceControllerTest {

    @MockBean
    private LocationQueryService locationQueryService;
    @MockBean
    private KakaoService kakaoService;

    private final MockMvc mvc;

    public MeetingPlaceControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("검색 키워드와 15개의 location 객체가 결과로 주어지고, 키워드로 장소를 검색하면, 검색된 장소들을 반환한다.")
    @Test
    void givenKeywordAnd15LocationsAsResult_whenSearchingByKeyword_thenReturnPlaces() throws Exception {
        // given
        long loginMemberId = 1L;
        String keyword = "서울";
        Pageable pageable = Pageable.ofSize(PAGE_SIZE_OF_SEARCHING_MEETING_PLACES);
        PageImpl<LocationDto> expectedResult = new PageImpl<>(List.of(
                LocationDto.of("서울특별시", "강동구", "암사제1동", new Point("", "127.132663")),
                LocationDto.of("서울특별시", "종로구", "숭인동", new Point("", "127.0156274")),
                LocationDto.of("서울특별시", "서초구", "방배2동", new Point("", "126.9855106")),
                LocationDto.of("서울특별시", "종로구", "연지동", new Point("", "127.0002")),
                LocationDto.of("서울특별시", "중구", "남대문로4가", new Point("", "126.975609")),
                LocationDto.of("서울특별시", "동대문구", "전농제2동", new Point("", "127.0600375")),
                LocationDto.of("서울특별시", "영등포구", "신길동", new Point("", "126.9214285")),
                LocationDto.of("서울특별시", "동작구", "상도1동", new Point("", "126.953089")),
                LocationDto.of("서울특별시", "종로구", "신교동", new Point("", "126.9678")),
                LocationDto.of("서울특별시", "종로구", "신영동", new Point("", "126.9621")),
                LocationDto.of("서울특별시", "양천구", "신정6동", new Point("", "126.8644471")),
                LocationDto.of("서울특별시", "용산구", "원효로2가", new Point("", "126.963225")),
                LocationDto.of("서울특별시", "영등포구", "양평제2동", new Point("", "126.8939535")),
                LocationDto.of("서울특별시", "종로구", "사직동", new Point("", "126.9688397")),
                LocationDto.of("서울특별시", "서대문구", "대신동", new Point("", "126.9459748"))
        ));
        given(locationQueryService.searchDtosByKeyword(keyword, pageable)).willReturn(expectedResult);

        // when & then
        mvc.perform(
                        get("/api/v1/meeting-places")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .queryParam("page", "0")
                                .queryParam("keyword", keyword)
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.numOfElements").value(15))
                .andExpect(jsonPath("$.contents").isArray())
                .andExpect(jsonPath("$.contents").isNotEmpty());
    }

    @DisplayName("검색 키워드와 15개의 kakao 장소 데이터가 결과로 주어지고, 키워드로 장소를 검색하면, 검색된 장소들을 반환한다.")
    @Test
    void givenKeywordAnd15KakaoPlacesAsResult_whenSearchingByKeyword_thenReturnPlaces() throws Exception {
        // given
        long loginMemberId = 1L;
        String keyword = "광교";
        Pageable pageable = Pageable.ofSize(PAGE_SIZE_OF_SEARCHING_MEETING_PLACES);
        List<KakaoPlaceInfo> expectedContent = List.of(
                KakaoPlaceInfo.of("광교역 신분당선", null, "http://place.map.kakao.com/27392590", "교통,수송 > 지하철,전철 > 신분당선", "경기 수원시 영통구 이의동 740-1", "경기 수원시 영통구 대학로 55", "27392590", "031-8018-7830", KakaoCategoryGroupCode.SW8, "127.044268853097", ""),
                KakaoPlaceInfo.of("광교저수지", null, "http://place.map.kakao.com/11034070", "여행 > 관광,명소 > 저수지", "경기 수원시 장안구 하광교동 351", "", "11034070", "", KakaoCategoryGroupCode.AT4, "127.02853807770109", ""),
                KakaoPlaceInfo.of("광교중앙역 신분당선", null, "http://place.map.kakao.com/27392591", "교통,수송 > 지하철,전철 > 신분당선", "경기 수원시 영통구 이의동 268-1", "경기 수원시 영통구 도청로 지하 45", "27392591", "031-8018-7820", KakaoCategoryGroupCode.SW8, "127.051726324729", ""),
                KakaoPlaceInfo.of("광교카페거리", null, "http://place.map.kakao.com/24535871", "여행 > 관광,명소 > 테마거리 > 카페거리", "경기 수원시 영통구 이의동 1381-4", "", "24535871", "", KakaoCategoryGroupCode.AT4, "127.051874100004", ""),
                KakaoPlaceInfo.of("광교마루길", null, "http://place.map.kakao.com/27610056", "여행 > 관광,명소 > 테마거리", "경기 수원시 장안구 하광교동", "", "27610056", "", KakaoCategoryGroupCode.AT4, "127.031979036898", ""),
                KakaoPlaceInfo.of("광교호수공원 프라이부르크전망대", null, "http://place.map.kakao.com/381494312", "여행 > 관광,명소 > 전망대", "경기 수원시 영통구 하동 1024", "경기 수원시 영통구 광교호수로 127", "381494312", "070-8800-2460", KakaoCategoryGroupCode.AT4, "127.065981860276", ""),
                KakaoPlaceInfo.of("광교고등학교", null, "http://place.map.kakao.com/14882947", "교육,학문 > 학교 > 고등학교", "경기 수원시 영통구 이의동 1346", "경기 수원시 영통구 도청로89번길 11", "14882947", "031-8061-8295", KakaoCategoryGroupCode.SC4, "127.04823999349", ""),
                KakaoPlaceInfo.of("광교중학교", null, "http://place.map.kakao.com/12802018", "교육,학문 > 학교 > 중학교", "경기 수원시 영통구 이의동 1207", "경기 수원시 영통구 웰빙타운로 55", "12802018", "031-218-3520", KakaoCategoryGroupCode.SC4, "127.044721440604", ""),
                KakaoPlaceInfo.of("광교어린이천문대", null, "http://place.map.kakao.com/854046920", "여행 > 관광,명소 > 천문대", "경기 수원시 영통구 이의동 1222-4", "경기 수원시 영통구 웰빙타운로36번길 46-248", "854046920", "031-216-3245", KakaoCategoryGroupCode.AT4, "127.05458522621", ""),
                KakaoPlaceInfo.of("광교호수중학교", null, "http://place.map.kakao.com/1163650833", "교육,학문 > 학교 > 중학교", "경기 수원시 영통구 원천동 596", "경기 수원시 영통구 월드컵로 8", "1163650833", "031-895-2800", KakaoCategoryGroupCode.SC4, "127.0637536780756", ""),
                KakaoPlaceInfo.of("광교초등학교", null, "http://place.map.kakao.com/12802084", "교육,학문 > 학교 > 초등학교", "경기 수원시 영통구 이의동 1208", "경기 수원시 영통구 대학로 91", "12802084", "031-217-7605", KakaoCategoryGroupCode.SC4, "127.04575876305618", ""),
                KakaoPlaceInfo.of("광교호수초등학교", null, "http://place.map.kakao.com/1439712290", "교육,학문 > 학교 > 초등학교", "경기 수원시 영통구 원천동 589-1", "경기 수원시 영통구 광교호수공원로 205", "1439712290", "031-201-1700", KakaoCategoryGroupCode.SC4, "127.05879256113157", ""),
                KakaoPlaceInfo.of("광교외식타운", null, "http://place.map.kakao.com/24829635", "여행 > 관광,명소 > 테마거리 > 먹자골목", "경기 수원시 영통구 이의동 1222-1", "경기 수원시 영통구 웰빙타운로36번길 46-220", "24829635", "", KakaoCategoryGroupCode.AT4, "127.054680109648", ""),
                KakaoPlaceInfo.of("광교저수지", null, "http://place.map.kakao.com/1927266944", "여행 > 관광,명소 > 저수지", "경기 수원시 장안구 상광교동 411", "", "1927266944", "", KakaoCategoryGroupCode.AT4, "127.020951966686", ""),
                KakaoPlaceInfo.of("광교골", null, "http://place.map.kakao.com/25181435", "여행 > 관광,명소 > 계곡", "경기 수원시 장안구 상광교동", "", "25181435", "", KakaoCategoryGroupCode.AT4, "127.015843158688", "")
        );
        given(locationQueryService.searchDtosByKeyword(keyword, pageable)).willReturn(Page.empty());
        given(kakaoService.searchKakaoPlacesByKeyword(keyword, pageable)).willReturn(new SliceImpl<>(expectedContent, pageable, false));

        // when & then
        mvc.perform(
                        get("/api/v1/meeting-places")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .queryParam("page", "0")
                                .queryParam("keyword", keyword)
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.numOfElements").value(15))
                .andExpect(jsonPath("$.contents").isArray())
                .andExpect(jsonPath("$.contents").isNotEmpty());
    }

    @DisplayName("검색 키워드와 함께 5개의 locations, 15개의 kakao 장소 데이터가 결과로 주어지고, 키워드로 장소를 검색하면, 검색된 장소들을 반환한다.")
    @Test
    void givenKeywordAnd3LocationsAnd15KakaoPlacesAsResult_whenSearchingByKeyword_thenReturnPlaces() throws Exception {
        // given
        long loginMemberId = 1L;
        String keyword = "수원";
        Pageable pageable = Pageable.ofSize(PAGE_SIZE_OF_SEARCHING_MEETING_PLACES);
        PageImpl<LocationDto> locations = new PageImpl<>(
                List.of(LocationDto.of("경기도", "수원시", null, new Point("37.263476", "127.028646")),
                        LocationDto.of("경기도", "수원시", "영통구", new Point("37.2596", "127.046525")),
                        LocationDto.of("경기도", "수원시", "팔달구", new Point("37.2825695", "127.0200976")),
                        LocationDto.of("경기도", "수원시", "권선구", new Point("37.257687", "126.971911")),
                        LocationDto.of("경기도", "수원시", "장안구", new Point("37.3039709", "127.0101225")))
        );
        SliceImpl<KakaoPlaceInfo> kakaoPlaces = new SliceImpl<>(
                List.of(KakaoPlaceInfo.of("서울대학교 수원수목원", null, "http://place.map.kakao.com/26624264", "여행 > 관광,명소 > 수목원,식물원", "경기 수원시 권선구 서둔동 92-6", "경기 수원시 권선구 서호로 16", "26624264", "", KakaoCategoryGroupCode.AT4, "126.982751815127", "37.2622966122058"),
                        KakaoPlaceInfo.of("수원팔색길 화성성곽길", null, "http://place.map.kakao.com/26564541", "여행 > 관광,명소 > 도보여행 > 수원팔색길", "경기 수원시 장안구 연무동", "", "26564541", "", KakaoCategoryGroupCode.AT4, "127.0187965332498", "37.28706778274413"),
                        KakaoPlaceInfo.of("일월저수지", null, "http://place.map.kakao.com/8058151", "여행 > 관광,명소 > 저수지", "경기 수원시 장안구 천천동", "", "8058151", "", KakaoCategoryGroupCode.AT4, "126.972831001286", "37.2882014656875"),
                        KakaoPlaceInfo.of("광교산산림욕장", null, "http://place.map.kakao.com/11475391", "여행 > 관광,명소 > 자연휴양림", "경기 수원시 장안구 조원동 산 6", "", "11475391", "031-228-4575", KakaoCategoryGroupCode.AT4, "127.021603970425", "37.3088399610759"),
                        KakaoPlaceInfo.of("광교호수공원 프라이부르크전망대", null, "http://place.map.kakao.com/381494312", "여행 > 관광,명소 > 전망대", "경기 수원시 영통구 하동 1024", "경기 수원시 영통구 광교호수로 127", "381494312", "070-8800-2460", KakaoCategoryGroupCode.AT4, "127.065981860276", "37.2806472011191"),
                        KakaoPlaceInfo.of("서호", null, "http://place.map.kakao.com/11141835", "여행 > 관광,명소 > 저수지", "경기 수원시 팔달구 화서동", "", "11141835", "", KakaoCategoryGroupCode.AT4, "126.987890079534", "37.2771031598479"),
                        KakaoPlaceInfo.of("수원공방거리", null, "http://place.map.kakao.com/27445985", "여행 > 관광,명소 > 테마거리", "경기 수원시 팔달구 남창동", "", "27445985", "", KakaoCategoryGroupCode.AT4, "127.015335023514", "37.2785877000022"),
                        KakaoPlaceInfo.of("신대호수", null, "http://place.map.kakao.com/11740028", "여행 > 관광,명소 > 호수", "경기 수원시 영통구 하동", "", "11740028", "", KakaoCategoryGroupCode.AT4, "127.073864045742", "37.2866425637606"),
                        KakaoPlaceInfo.of("원천호수", null, "http://place.map.kakao.com/8473582", "여행 > 관광,명소 > 호수", "경기 수원시 영통구 하동", "", "8473582", "", KakaoCategoryGroupCode.AT4, "127.063203398409", "37.2792494027177"),
                        KakaoPlaceInfo.of("화서역먹자골목", null, "http://place.map.kakao.com/27469648", "여행 > 관광,명소 > 테마거리 > 먹자골목", "경기 수원시 팔달구 화서동 697", "", "27469648", "", KakaoCategoryGroupCode.AT4, "126.988984571545", "37.2873246575744"),
                        KakaoPlaceInfo.of("수원팔색길 여우길", null, "http://place.map.kakao.com/27598174", "여행 > 관광,명소 > 도보여행 > 수원팔색길", "경기 수원시 영통구 이의동 1378-27", "", "27598174", "", KakaoCategoryGroupCode.AT4, "127.04658754556398", "37.286955586735615"),
                        KakaoPlaceInfo.of("만석공원 만석거", null, "http://place.map.kakao.com/17807777", "여행 > 관광,명소 > 저수지", "경기 수원시 장안구 송죽동 414", "", "17807777", "", KakaoCategoryGroupCode.AT4, "127.000956453782", "37.2999180684856"),
                        KakaoPlaceInfo.of("포시즌힐링팜", null, "http://place.map.kakao.com/1176369644", "여행 > 관광,명소 > 관광농원", "경기 수원시 권선구 입북동 584", "", "1176369644", "031-227-3555", KakaoCategoryGroupCode.AT4, "126.957867807748", "37.2893179770889"),
                        KakaoPlaceInfo.of("호매실카페거리", null, "http://place.map.kakao.com/304906995", "여행 > 관광,명소 > 테마거리 > 카페거리", "경기 수원시 권선구 호매실동 1425-1", "", "304906995", "", KakaoCategoryGroupCode.AT4, "126.943121248332", "37.2687284572022"),
                        KakaoPlaceInfo.of("하광교소류지", null, "http://place.map.kakao.com/18347208", "여행 > 관광,명소 > 저수지", "경기 수원시 장안구 하광교동 1", "", "18347208", "", KakaoCategoryGroupCode.AT4, "127.027574855137", "37.324934793141")),
                pageable,
                true
        );
        given(locationQueryService.searchDtosByKeyword(keyword, pageable)).willReturn(locations);
        given(kakaoService.searchKakaoPlacesByKeyword(keyword, pageable)).willReturn(kakaoPlaces);

        // when & then
        mvc.perform(
                        get("/api/v1/meeting-places")
                                .header(API_MINOR_VERSION_HEADER_NAME, 1)
                                .queryParam("page", "0")
                                .queryParam("keyword", keyword)
                                .with(user(createTestUserDetails(loginMemberId)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.numOfElements").value(20))
                .andExpect(jsonPath("$.contents").isArray())
                .andExpect(jsonPath("$.contents").isNotEmpty());
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