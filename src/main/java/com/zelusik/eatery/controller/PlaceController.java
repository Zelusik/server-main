package com.zelusik.eatery.controller;

import com.zelusik.eatery.constant.place.DayOfWeek;
import com.zelusik.eatery.constant.place.PlaceSearchKeyword;
import com.zelusik.eatery.dto.SliceResponse;
import com.zelusik.eatery.dto.place.request.PlaceCreateRequest;
import com.zelusik.eatery.dto.place.response.*;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Tag(name = "장소 관련 API")
@RequiredArgsConstructor
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
            @ApiResponse(description = "[1350] 장소에 대한 추가 정보를 스크래핑 할 Flask 서버에서 에러가 발생한 경우.", responseCode = "500", content = @Content),
            @ApiResponse(description = "[3000] 상세 페이지에서 읽어온 장소 영업시간이 처리할 수 없는 형태일 경우.", responseCode = "500", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PlaceResponse> save(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody PlaceCreateRequest request
    ) {
        PlaceResponse response = PlaceResponse.from(placeService.createAndReturnDto(userPrincipal.getMemberId(), request));

        return ResponseEntity
                .created(URI.create("/api/places/" + response.getId()))
                .body(response);
    }

    @Operation(
            summary = "장소 단건 조회",
            description = "장소의 PK를 받아 해당하는 장소 정보를 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200", content = @Content(schema = @Schema(implementation = PlaceResponse.class))),
            @ApiResponse(description = "[3001] 찾고자 하는 장소가 존재하지 않는 경우", responseCode = "404", content = @Content)
    })
    @GetMapping("/{placeId}")
    public PlaceResponseWithImages find(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long placeId
    ) {
        return PlaceResponseWithImages.from(placeService.findDtoById(userPrincipal.getMemberId(), placeId));
    }

    @Operation(
            summary = "주변 장소 검색 (거리순 정렬)",
            description = "<p>중심 좌표를 받아 중심 좌표에서 가까운 장소들을 검색합니다." +
                    "<p>주변 3km 내에 있는 장소만 우선적으로 검색하며, 3km 이내에 아무런 장소가 없다면 10km로 검색 범위를 확대해 다시 검색합니다." +
                    "<p>요청 데이터 중 <code>daysOfWeek</code>가 없으면 전체 날짜에 대해, <code>keyword</code>가 없으면 전체 약속 상황에 대해 검색합니다." +
                    "<p>현재 \"약속 상황\" 필터링은 미구현 상태입니다. (구현 예정)",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping("/search")
    public SliceResponse<PlaceCompactResponseWithImages> searchNearBy(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "요일 목록",
                    example = "월,화,수"
            ) @RequestParam(required = false) List<String> daysOfWeek,
            @Parameter(
                    description = "약속 상황",
                    example = "신나는"
            ) @RequestParam(required = false) String keyword,
            @Parameter(
                    description = "중심 위치 - 위도",
                    example = "37.566826004661"
            ) @RequestParam String lat,
            @Parameter(
                    description = "중심 위치 - 경도",
                    example = "126.978652258309"
            ) @RequestParam String lng,
            @Parameter(
                    description = "페이지 번호(0부터 시작합니다). 기본값은 0입니다.",
                    example = "0"
            ) @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(
                    description = "한 페이지에 담긴 데이터의 최대 개수(사이즈). 기본값은 30입니다.",
                    example = "30"
            ) @RequestParam(required = false, defaultValue = "30") int size
    ) {
        return new SliceResponse<PlaceCompactResponseWithImages>().from(
                placeService.findDtosNearBy(
                        userPrincipal.getMemberId(),
                        daysOfWeek == null ? null : daysOfWeek.stream().map(DayOfWeek::valueOfDescription).toList(),
                        keyword == null ? null : PlaceSearchKeyword.valueOfDescription(keyword),
                        lat, lng,
                        PageRequest.of(page, size)
                ).map(PlaceCompactResponseWithImages::from)
        );
    }

    @Operation(
            summary = "북마크에 저장한 장소 조회",
            description = "<p>북마크에 저장한 장소들을 조회합니다." +
                    "<p>정렬 기준은 최근에 북마크에 저장한 순서입니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping("/bookmarks")
    public SliceResponse<MarkedPlaceResponse> findMarkedPlaces(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(
                    description = "페이지 번호(0부터 시작합니다). 기본값은 0입니다.",
                    example = "0"
            ) @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(
                    description = "한 페이지에 담긴 데이터의 최대 개수(사이즈). 기본값은 20입니다.",
                    example = "20"
            ) @RequestParam(required = false, defaultValue = "20") int size
    ) {
        return new SliceResponse<MarkedPlaceResponse>().from(
                placeService.findMarkedDtos(
                        userPrincipal.getMemberId(),
                        PageRequest.of(page, size)
                ).map(MarkedPlaceResponse::from)
        );
    }

    @Operation(
            summary = "저장한 장소들에 대한 필터링 키워드 조회",
            description = "<p>저장한 장소들에 대한 필터링 키워드를 조회합니다." +
                    "<p>필터링 키워드에 대한 설명은 <strong><a href=\"https://www.notion.so/asdfqweasd/f6f39969ea1e48f8afee61e696e4d038?pvs=4\">[노션]데이터</a> - MY 저장 페이지: 상단버튼</strong>을 참고해주세요.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping("/bookmarks/filtering-keywords")
    public PlaceFilteringKeywordListResponse getFilteringKeywords(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<PlaceFilteringKeywordResponse> filteringKeywords = placeService.getFilteringKeywords(userPrincipal.getMemberId()).stream()
                .map(PlaceFilteringKeywordResponse::from)
                .toList();
        return PlaceFilteringKeywordListResponse.of(filteringKeywords);
    }
}
