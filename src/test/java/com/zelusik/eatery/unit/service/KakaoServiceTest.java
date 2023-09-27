package com.zelusik.eatery.unit.service;

import com.zelusik.eatery.global.kakao.dto.KakaoPlaceInfo;
import com.zelusik.eatery.global.kakao.service.KakaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.zelusik.eatery.domain.meeting_place.api.MeetingPlaceController.PAGE_SIZE_OF_SEARCHING_MEETING_PLACES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("[Unit] Kakao Service")
@ActiveProfiles("test")
@AutoConfigureWebClient(registerRestTemplate = true)
@RestClientTest(KakaoService.class)
class KakaoServiceTest {

    private final KakaoService sut;
    private final MockRestServiceServer restServer;

    @Value("${kakao.rest-api.key}")
    private String apiKey;

    @Autowired
    public KakaoServiceTest(MockRestServiceServer restServer, KakaoService sut) {
        this.restServer = restServer;
        this.sut = sut;
    }

    @DisplayName("검색 키워드가 주어지고, 카카오에서 키워드로 장소들을 검색하면, 검색된 장소들이 반환된다.")
    @Test
    void givenKeyword_whenSearchingPlacesFromKakao_thenReturnSearchingResult() {
        // given
        String keyword = "서울";
        Pageable pageable = Pageable.ofSize(PAGE_SIZE_OF_SEARCHING_MEETING_PLACES);
        URI requestUri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com/v2/local/search/keyword.json")
                .queryParam("page", pageable.getPageNumber() + 1)
                .queryParam("size", pageable.getPageSize())
                .queryParam("category_group_code", "SW8,AT4,SC4")
                .queryParam("query", keyword)
                .encode()
                .build().toUri();
        String expectedKakaoResponse = """
                {
                    "documents":[
                        {
                            "address_name":"서울 용산구 이촌동 302-146",
                            "category_group_code":"AT4",
                            "category_group_name":"관광명소",
                            "category_name":"여행 > 관광,명소 > 섬 > 섬(내륙)",
                            "distance":"",
                            "id":"8252248",
                            "phone":"02-749-4500",
                            "place_name":"노들섬",
                            "place_url":"http://place.map.kakao.com/8252248",
                            "road_address_name":"서울 용산구 양녕로 445",
                            "x":"126.95803386590158",
                            "y":"37.51766013568054"
                        },
                        {
                            "address_name":"서울 도봉구 방학동",
                            "category_group_code":"AT4",
                            "category_group_name":"관광명소",
                            "category_name":"여행 > 관광,명소 > 도보여행 > 둘레길 > 북한산둘레길",
                            "distance":"",
                            "id":"18580965",
                            "phone":"",
                            "place_name":"북한산둘레길 왕실묘역길20구간",
                            "place_url":"http://place.map.kakao.com/18580965",
                            "road_address_name":"",
                            "x":"127.0186775926887",
                            "y":"37.661988874268154"
                        },
                        {
                            "address_name":"서울 종로구 와룡동 2-1",
                            "category_group_code":"AT4",
                            "category_group_name":"관광명소",
                            "category_name":"여행 > 관광,명소 > 문화유적 > 고궁,궁",
                            "distance":"",
                            "id":"11156260",
                            "phone":"02-762-4868",
                            "place_name":"창경궁",
                            "place_url":"http://place.map.kakao.com/11156260",
                            "road_address_name":"서울 종로구 창경궁로 185",
                            "x":"126.995199911733",
                            "y":"37.5794165384496"
                        },
                        {
                            "address_name":"서울 강남구 신사동 668-33",
                            "category_group_code":"AT4",
                            "category_group_name":"관광명소",
                            "category_name":"여행 > 관광,명소 > 테마거리",
                            "distance":"",
                            "id":"7990409",
                            "phone":"02-3445-6402",
                            "place_name":"압구정로데오거리",
                            "place_url":"http://place.map.kakao.com/7990409",
                            "road_address_name":"",
                            "x":"127.039152029523",
                            "y":"37.5267558230172"
                        },
                        {
                            "address_name":"서울 송파구 잠실동 47",
                            "category_group_code":"AT4",
                            "category_group_name":"관광명소",
                            "category_name":"여행 > 관광,명소 > 호수",
                            "distance":"",
                            "id":"7947003",
                            "phone":"",
                            "place_name":"석촌호수 서호",
                            "place_url":"http://place.map.kakao.com/7947003",
                            "road_address_name":"",
                            "x":"127.099112837006",
                            "y":"37.5076807262772"
                        },
                        {
                            "address_name":"서울 종로구 관훈동",
                            "category_group_code":"AT4",
                            "category_group_name":"관광명소",
                            "category_name":"여행 > 관광,명소 > 테마거리",
                            "distance":"",
                            "id":"8053121",
                            "phone":"",
                            "place_name":"인사동거리",
                            "place_url":"http://place.map.kakao.com/8053121",
                            "road_address_name":"",
                            "x":"126.98561901337",
                            "y":"37.5733610774662"
                        },
                        {
                            "address_name":"서울 영등포구 여의도동",
                            "category_group_code":"AT4",
                            "category_group_name":"관광명소",
                            "category_name":"여행 > 관광,명소 > 강",
                            "distance":"",
                            "id":"13121007",
                            "phone":"",
                            "place_name":"한강",
                            "place_url":"http://place.map.kakao.com/13121007",
                            "road_address_name":"",
                            "x":"126.947545050571",
                            "y":"37.5250892160129"
                        },
                        {
                            "address_name":"서울 영등포구 여의도동",
                            "category_group_code":"AT4",
                            "category_group_name":"관광명소",
                            "category_name":"여행 > 관광,명소 > 테마거리",
                            "distance":"",
                            "id":"8003939",
                            "phone":"",
                            "place_name":"여의서로",
                            "place_url":"http://place.map.kakao.com/8003939",
                            "road_address_name":"",
                            "x":"126.91493215270192",
                            "y":"37.53351080966769"
                        },
                        {
                            "address_name":"서울 성동구 응봉동",
                            "category_group_code":"AT4",
                            "category_group_name":"관광명소",
                            "category_name":"여행 > 관광,명소 > 산",
                            "distance":"",
                            "id":"10666639",
                            "phone":"",
                            "place_name":"응봉산",
                            "place_url":"http://place.map.kakao.com/10666639",
                            "road_address_name":"",
                            "x":"127.029834156041",
                            "y":"37.5482528089186"
                        },
                        {
                            "address_name":"서울 마포구 서교동 348-78",
                            "category_group_code":"AT4",
                            "category_group_name":"관광명소",
                            "category_name":"여행 > 관광,명소 > 테마거리",
                            "distance":"",
                            "id":"17169006",
                            "phone":"02-337-7361",
                            "place_name":"홍대패션거리",
                            "place_url":"http://place.map.kakao.com/17169006",
                            "road_address_name":"",
                            "x":"126.923580878826",
                            "y":"37.5553965854703"
                        },
                        {
                            "address_name":"서울 송파구 신천동 32",
                            "category_group_code":"AT4",
                            "category_group_name":"관광명소",
                            "category_name":"여행 > 관광,명소 > 호수",
                            "distance":"",
                            "id":"27212955",
                            "phone":"",
                            "place_name":"석촌호수 동호",
                            "place_url":"http://place.map.kakao.com/27212955",
                            "road_address_name":"",
                            "x":"127.105899949393",
                            "y":"37.5116212351376"
                        },
                        {
                            "address_name":"서울 중구 명동2가",
                            "category_group_code":"AT4",
                            "category_group_name":"관광명소",
                            "category_name":"여행 > 관광,명소 > 테마거리",
                            "distance":"",
                            "id":"7876521",
                            "phone":"",
                            "place_name":"명동거리",
                            "place_url":"http://place.map.kakao.com/7876521",
                            "road_address_name":"",
                            "x":"126.984901336292",
                            "y":"37.5620769169639"
                        },
                        {
                            "address_name":"서울 송파구 신천동 29",
                            "category_group_code":"AT4",
                            "category_group_name":"관광명소",
                            "category_name":"여행 > 관광,명소 > 전망대",
                            "distance":"",
                            "id":"680691609",
                            "phone":"1661-2000",
                            "place_name":"서울스카이",
                            "place_url":"http://place.map.kakao.com/680691609",
                            "road_address_name":"서울 송파구 올림픽로 300",
                            "x":"127.102544369423",
                            "y":"37.5126729644342"
                        },
                        {
                            "address_name":"서울 성북구 삼선동1가",
                            "category_group_code":"AT4",
                            "category_group_name":"관광명소",
                            "category_name":"여행 > 관광,명소 > 도보여행 > 서울한양도성",
                            "distance":"",
                            "id":"18741277",
                            "phone":"",
                            "place_name":"서울한양도성 낙산구간2코스",
                            "place_url":"http://place.map.kakao.com/18741277",
                            "road_address_name":"",
                            "x":"127.00863578989066",
                            "y":"37.58079664277135"
                        },
                        {
                            "address_name":"서울 광진구 능동 18",
                            "category_group_code":"AT4",
                            "category_group_name":"관광명소",
                            "category_name":"여행 > 관광,명소 > 동물원",
                            "distance":"",
                            "id":"17556470",
                            "phone":"02-450-9311",
                            "place_name":"서울어린이대공원 동물원",
                            "place_url":"http://place.map.kakao.com/17556470",
                            "road_address_name":"",
                            "x":"127.082318892024",
                            "y":"37.5482386958136"
                        }
                    ],
                    "meta":{
                        "is_end":false,
                        "pageable_count":45,
                        "same_name":{
                            "keyword":"",
                            "region":[],
                            "selected_region":"서울특별시"
                        },
                        "total_count":2780
                    }
                }
                """;
        restServer.expect(requestTo(requestUri))
                .andExpect(header("Authorization", "KakaoAK " + apiKey))
                .andRespond(withSuccess(
                        expectedKakaoResponse,
                        MediaType.APPLICATION_JSON
                ));

        // when
        Slice<KakaoPlaceInfo> result = sut.searchKakaoPlacesByKeyword(keyword, pageable);

        // then
        restServer.verify();
        assertThat(result.getNumberOfElements()).isEqualTo(15);
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.hasNext()).isTrue();
    }
}