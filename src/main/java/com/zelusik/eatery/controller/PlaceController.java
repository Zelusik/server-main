package com.zelusik.eatery.controller;

import com.zelusik.eatery.constant.FoodCategoryValue;
import com.zelusik.eatery.constant.place.DayOfWeek;
import com.zelusik.eatery.constant.place.FilteringType;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.place.Point;
import com.zelusik.eatery.dto.PageResponse;
import com.zelusik.eatery.dto.SliceResponse;
import com.zelusik.eatery.dto.place.PlaceDto;
import com.zelusik.eatery.dto.place.request.PlaceCreateRequest;
import com.zelusik.eatery.dto.place.response.*;
import com.zelusik.eatery.exception.review.InvalidTypeOfReviewKeywordValueException;
import com.zelusik.eatery.security.UserPrincipal;
import com.zelusik.eatery.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.net.URI;
import java.util.List;

@Tag(name = "장소 관련 API")
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/places")
@RestController
public class PlaceController {

    private final PlaceService placeService;

    @Operation(
            summary = "장소 저장",
            description = "장소 정보를 전달받아 장소를 서버에 저장합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "Created", responseCode = "201", content = @Content(schema = @Schema(implementation = PlaceResponse.class))),
            @ApiResponse(description = "[3000] 동일한 장소 데이터가 이미 존재하는 경우", responseCode = "409", content = @Content),
            @ApiResponse(description = "[1350] 장소에 대한 추가 정보를 추출할 Scraping server에서 에러가 발생한 경우.", responseCode = "500", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PlaceResponse> save(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody PlaceCreateRequest request
    ) {
        PlaceResponse response = PlaceResponse.from(placeService.create(userPrincipal.getMemberId(), request));

        return ResponseEntity
                .created(URI.create("/api/places/" + response.getId()))
                .body(response);
    }

    @Operation(
            summary = "장소 단건 조회 - PK",
            description = "장소의 PK를 받아 해당하는 장소 정보를 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200", content = @Content(schema = @Schema(implementation = PlaceResponse.class))),
            @ApiResponse(description = "[3001] 찾고자 하는 장소가 존재하지 않는 경우", responseCode = "404", content = @Content)
    })
    @GetMapping("/{placeId}")
    public FindPlaceResponse findPlaceById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "PK of place",
                    example = "3"
            ) @PathVariable Long placeId
    ) {
        PlaceDto placeDtos = placeService.findDtoWithMarkedStatusAndImagesById(userPrincipal.getMemberId(), placeId);
        return FindPlaceResponse.from(placeDtos);
    }

    @Operation(
            summary = "장소 단건 조회 - kakao place unique id",
            description = "장소의 고유 id(<code>kakaoPid</code>)를 받아 해당하는 장소 정보를 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200", content = @Content(schema = @Schema(implementation = PlaceResponse.class))),
            @ApiResponse(description = "[3001] 찾고자 하는 장소가 존재하지 않는 경우", responseCode = "404", content = @Content)
    })
    @GetMapping
    public FindPlaceResponse findPlaceByKakaoPid(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "Kakao place unique id",
                    example = "263830255"
            ) @RequestParam @NotBlank String kakaoPid
    ) {
        PlaceDto placeDtos = placeService.findDtoWithMarkedStatusAndImagesByKakaoPid(userPrincipal.getMemberId(), kakaoPid);
        return FindPlaceResponse.from(placeDtos);
    }

    @Operation(
            summary = "키워드로 장소 검색하기",
            description = "키워드로 장소를 검색한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping("/search")
    public SliceResponse<SearchPlacesByKeywordResponse> searchPlacesByKeyword(
            @Parameter(
                    description = "검색 키워드",
                    example = "강남"
            ) @RequestParam @NotEmpty String keyword,
            @Parameter(
                    description = "페이지 번호 (0부터 시작)",
                    example = "0"
            ) @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(
                    description = "한 페이지에 담긴 데이터의 최대 개수(사이즈)",
                    example = "30"
            ) @RequestParam(required = false, defaultValue = "30") int size
    ) {
        Slice<PlaceDto> placeDtos = placeService.searchDtosByKeyword(keyword, PageRequest.of(page, size));
        return new SliceResponse<SearchPlacesByKeywordResponse>().from(placeDtos.map(SearchPlacesByKeywordResponse::from));
    }

    @Operation(
            summary = "주변 장소 검색 (거리순 정렬)",
            description = "중심 좌표를 받아 중심 좌표에서 가까운 장소들을 검색합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping("/near")
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "[3505] <code>preferredVibe</code>에 분위기에 대한 내용이 아닌 값이 주어진 경우", responseCode = "400", content = @Content)
    })
    public PageResponse<FindNearPlacesResponse> findNearPlaces(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "중심 위치 - 위도",
                    example = "37.566826004661"
            ) @RequestParam String lat,
            @Parameter(
                    description = "중심 위치 - 경도",
                    example = "126.978652258309"
            ) @RequestParam String lng,
            @Parameter(
                    description = "(필터링 조건) 음식 카테고리",
                    example = "KOREAN"
            ) @RequestParam(required = false) @Nullable FoodCategoryValue foodCategory,
            @Parameter(
                    description = "(필터링 조건) 요일 목록",
                    example = "[\"MON\", \"TUE\", \"WED\"]"
            ) @RequestParam(required = false) @Nullable List<DayOfWeek> daysOfWeek,
            @Parameter(
                    description = """
                            <p>(필터링 조건) 선호하는 분위기. 가능한 값은 다음과 같다.</p>
                            <ul>
                                <li><code>WITH_ALCOHOL</code>: 술과 함께하기 좋은</li>
                                <li><code>GOOD_FOR_DATE</code>: 데이트 하기에 좋은</li>
                                <li><code>WITH_ELDERS</code>: 웃어른과 함께하기 좋은</li>
                                <li><code>CAN_ALONE</code>: 혼밥 가능한</li>
                                <li><code>PERFECT_FOR_GROUP_MEETING</code>: 단체 모임에 좋은</li>
                                <li><code>WAITING</code>: 웨이팅 있는</li>
                                <li><code>SILENT</code>: 조용조용한</li>
                                <li><code>NOISY</code>: 왁자지껄한</li>
                             </ul>
                             """,
                    example = "WITH_ALCOHOL"
            ) @RequestParam(required = false) @Nullable ReviewKeywordValue preferredVibe,
            @Parameter(
                    description = "페이지 번호 (0부터 시작)",
                    example = "0"
            ) @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(
                    description = "한 페이지에 담긴 데이터의 최대 개수(사이즈)",
                    example = "30"
            ) @RequestParam(required = false, defaultValue = "30") int size
    ) {
        if (preferredVibe != null && !preferredVibe.getType().equals(ReviewKeywordValue.ReviewKeywordType.VIBE)) {
            throw new InvalidTypeOfReviewKeywordValueException("분위기에 대한 값만 사용할 수 있습니다.");
        }

        Page<PlaceDto> searchedPlaceDtos = placeService.findDtosNearBy(userPrincipal.getMemberId(), foodCategory, daysOfWeek, preferredVibe, new Point(lat, lng), PageRequest.of(page, size));
        return new PageResponse<FindNearPlacesResponse>()
                .from(searchedPlaceDtos.map(FindNearPlacesResponse::from));
    }

    @Operation(
            summary = "저장한 장소들에 대한 필터링 키워드 조회",
            description = "<p>저장한 장소들에 대한 필터링 키워드를 조회합니다." +
                          "<p>필터링 키워드에 대한 설명은 <strong><a href=\"https://www.notion.so/asdfqweasd/f6f39969ea1e48f8afee61e696e4d038?pvs=4\">[노션]데이터</a> - MY 저장 페이지: 상단버튼</strong>을 참고해주세요.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping("/bookmarks/filtering-keywords")
    public PlaceFilteringKeywordListResponse getFilteringKeywords(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<PlaceFilteringKeywordResponse> filteringKeywords = placeService.getFilteringKeywords(userPrincipal.getMemberId()).stream()
                .map(PlaceFilteringKeywordResponse::from)
                .toList();
        return PlaceFilteringKeywordListResponse.of(filteringKeywords);
    }

    @Operation(
            summary = "북마크에 저장한 장소 조회",
            description = "<p>북마크에 저장한 장소들을 조회합니다." +
                          "<p>정렬 기준은 최근에 북마크에 저장한 순서입니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping("/bookmarks")
    public PageResponse<FindMarkedPlacesResponse> findMarkedPlaces(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "<p>Filtering 조건 유형. 값은 다음과 같습니다." +
                                  "<ul>" +
                                  "<li><code>FIRST_CATEGORY</code>: 음식 카테고리(first category). 한식, 일식 등)</li>" +
                                  "<li><code>SECOND_CATEGORY</code>: 음식 카테고리(second category) 햄버거, 피자, 국밥 등</li>" +
                                  "<li><code>TOP_3_KEYWORDS</code>: 장소의 top 3 keyword</li>" +
                                  "<li><code>ADDRESS</code>: 장소의 주소 (ex. 영통구, 연남동 등)</li>" +
                                  "</ul>",
                    example = "CATEGORY"
            ) @RequestParam(required = false, defaultValue = "NONE") FilteringType type,
            @Parameter(
                    description = "<p>필터링 키워드 조회 API(<code>/api/places/bookmarks/filtering-keywords</code>)에서 전달받은 filtering keyword",
                    example = "육류,고기"
            ) @RequestParam(required = false) String keyword,
            @Parameter(
                    description = "페이지 번호(0부터 시작합니다). 기본값은 0입니다.",
                    example = "0"
            ) @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(
                    description = "한 페이지에 담긴 데이터의 최대 개수(사이즈). 기본값은 20입니다.",
                    example = "20"
            ) @RequestParam(required = false, defaultValue = "20") int size
    ) {
        if (type == FilteringType.TOP_3_KEYWORDS) {
            keyword = ReviewKeywordValue.valueOfDescription(keyword).toString();
        }
        Page<PlaceDto> markedPlaceDtos = placeService.findMarkedDtos(userPrincipal.getMemberId(), type, keyword, PageRequest.of(page, size));
        return new PageResponse<FindMarkedPlacesResponse>().from(markedPlaceDtos.map(FindMarkedPlacesResponse::from));
    }
}
