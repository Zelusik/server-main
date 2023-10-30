package com.zelusik.eatery.domain.place.api;

import com.zelusik.eatery.domain.place.constant.FilteringType;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.place.dto.PlaceWithMarkedStatusAndImagesDto;
import com.zelusik.eatery.domain.place.dto.request.FindNearPlacesFilteringConditionRequest;
import com.zelusik.eatery.domain.place.dto.request.PlaceCreateRequest;
import com.zelusik.eatery.domain.place.dto.response.*;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.place.service.PlaceCommandService;
import com.zelusik.eatery.domain.place.service.PlaceQueryService;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review.exception.InvalidTypeOfReviewKeywordValueException;
import com.zelusik.eatery.global.common.dto.response.PageResponse;
import com.zelusik.eatery.global.common.dto.response.SliceResponse;
import com.zelusik.eatery.global.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.net.URI;
import java.util.List;

import static com.zelusik.eatery.global.common.constant.EateryConstants.API_MINOR_VERSION_HEADER_NAME;

@Tag(name = "장소 관련 API")
@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
@RestController
public class PlaceController {

    private final PlaceCommandService placeCommandService;
    private final PlaceQueryService placeQueryService;

    @Operation(
            summary = "장소 저장",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>장소 정보를 전달받아 장소를 서버에 저장합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "Created", responseCode = "201"),
            @ApiResponse(description = "[3000] 동일한 장소 데이터가 이미 존재하는 경우", responseCode = "409", content = @Content),
            @ApiResponse(description = "[1350] 장소에 대한 추가 정보를 추출할 Scraping server에서 에러가 발생한 경우.", responseCode = "500", content = @Content)
    })
    @PostMapping(value = "/v1/places", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public ResponseEntity<SavePlaceResponse> savePlaceV1_1(@Valid @RequestBody PlaceCreateRequest request) {
        PlaceDto placeDto = placeCommandService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/places/" + placeDto.getId()))
                .body(SavePlaceResponse.from(placeDto));
    }

    @Operation(
            summary = "장소 단건 조회 - PK",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>장소의 PK를 받아 해당하는 장소 정보를 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "[3001] 찾고자 하는 장소가 존재하지 않는 경우", responseCode = "404", content = @Content)
    })
    @GetMapping(value = "/v1/places/{placeId}", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public FindPlaceResponse findPlaceByIdV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "PK of place",
                    example = "3"
            ) @PathVariable Long placeId
    ) {
        PlaceWithMarkedStatusAndImagesDto placeDtos = placeQueryService.findDtoWithMarkedStatusAndImagesById(userPrincipal.getMemberId(), placeId);
        return FindPlaceResponse.from(placeDtos);
    }

    @Operation(
            summary = "장소 단건 조회 - kakao place unique id",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>장소의 고유 id(<code>kakaoPid</code>)를 받아 해당하는 장소 정보를 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "[3001] 찾고자 하는 장소가 존재하지 않는 경우", responseCode = "404", content = @Content)
    })
    @GetMapping(value = "/v1/places", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public FindPlaceResponse findPlaceByKakaoPidV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "Kakao place unique id",
                    example = "263830255"
            ) @RequestParam @NotBlank String kakaoPid
    ) {
        PlaceWithMarkedStatusAndImagesDto placeDtos = placeQueryService.findDtoWithMarkedStatusAndImagesByKakaoPid(userPrincipal.getMemberId(), kakaoPid);
        return FindPlaceResponse.from(placeDtos);
    }

    @Operation(
            summary = "키워드로 장소 검색하기",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>키워드로 장소를 검색한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/v1/places/search", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public SliceResponse<SearchPlacesByKeywordResponse> searchPlacesByKeywordV1_1(
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
        Slice<PlaceDto> placeDtos = placeQueryService.searchDtosByKeyword(keyword, PageRequest.of(page, size));
        return new SliceResponse<SearchPlacesByKeywordResponse>().from(placeDtos.map(SearchPlacesByKeywordResponse::from));
    }

    @Operation(
            summary = "주변 장소 검색 (거리순 정렬)",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>중심 좌표를 받아 중심 좌표에서 가까운 장소들을 검색합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/v1/places/near", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "[3505] <code>preferredVibe</code>에 분위기에 대한 내용이 아닌 값이 주어진 경우", responseCode = "400", content = @Content)
    })
    public PageResponse<FindNearPlacesResponse> findNearPlacesV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "중심 위치 - 위도",
                    example = "37.566826004661"
            ) @RequestParam @NotBlank String lat,

            @Parameter(
                    description = "중심 위치 - 경도",
                    example = "126.978652258309"
            ) @RequestParam @NotBlank String lng,
            @ParameterObject @ModelAttribute @Valid FindNearPlacesFilteringConditionRequest filteringCondition,
            @Parameter(
                    description = "페이지 번호 (0부터 시작)",
                    example = "0"
            ) @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(
                    description = "한 페이지에 담긴 데이터의 최대 개수(사이즈)",
                    example = "30"
            ) @RequestParam(required = false, defaultValue = "10") int size
    ) {
        if (filteringCondition.getPreferredVibe() != null
            && !filteringCondition.getPreferredVibe().getType().equals(ReviewKeywordValue.ReviewKeywordType.VIBE)) {
            throw new InvalidTypeOfReviewKeywordValueException("분위기에 대한 값만 사용할 수 있습니다.");
        }

        Page<PlaceWithMarkedStatusAndImagesDto> searchedPlaceDtos = placeQueryService.findDtosWithoutOpeningHoursNearBy(userPrincipal.getMemberId(), filteringCondition, new Point(lat, lng), PageRequest.of(page, size));
        return new PageResponse<FindNearPlacesResponse>()
                .from(searchedPlaceDtos.map(FindNearPlacesResponse::from));
    }

    @Operation(
            summary = "저장한 장소들에 대한 필터링 키워드 조회",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>저장한 장소들에 대한 필터링 키워드를 조회합니다." +
                          "<p>필터링 키워드에 대한 설명은 <strong><a href=\"https://www.notion.so/asdfqweasd/f6f39969ea1e48f8afee61e696e4d038?pvs=4\">[노션]데이터</a> - MY 저장 페이지: 상단버튼</strong>을 참고해주세요.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/v1/places/bookmarks/filtering-keywords", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public PlaceFilteringKeywordListResponse getFilteringKeywordsV1_1(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<PlaceFilteringKeywordResponse> filteringKeywords = placeQueryService.getFilteringKeywords(userPrincipal.getMemberId()).stream()
                .map(PlaceFilteringKeywordResponse::from)
                .toList();
        return PlaceFilteringKeywordListResponse.of(filteringKeywords);
    }

    @Operation(
            summary = "북마크에 저장한 장소 조회",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>북마크에 저장한 장소들을 조회합니다." +
                          "<p>정렬 기준은 최근에 북마크에 저장한 순서입니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/v1/places/bookmarks", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public PageResponse<FindMarkedPlacesResponse> findMarkedPlacesV1_1(
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
            keyword = ReviewKeywordValue.valueOfContent(keyword).toString();
        }
        Page<PlaceWithMarkedStatusAndImagesDto> markedPlaceDtos = placeQueryService.findMarkedDtosWithoutOpeningHours(userPrincipal.getMemberId(), type, keyword, PageRequest.of(page, size));
        return new PageResponse<FindMarkedPlacesResponse>().from(markedPlaceDtos.map(FindMarkedPlacesResponse::from));
    }

    @Operation(
            summary = "kakaoPid로 장소 존재고 여부 조회하기",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p><code>kakaoPid</code>로 장소가 DB에 존재하는지 여부를 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/v1/places/existence", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public GetExistenceOfPlaceResponse getExistenceOfPlaceByKakaoPidV1_1(
            @Parameter(
                    description = "Kakao place unique id",
                    example = "263830255"
            ) @RequestParam @NotBlank String kakaoPid
    ) {
        boolean existenceOfPlaceByKakaoPid = placeQueryService.existsByKakaoPid(kakaoPid);
        return new GetExistenceOfPlaceResponse(existenceOfPlaceByKakaoPid);
    }
}
