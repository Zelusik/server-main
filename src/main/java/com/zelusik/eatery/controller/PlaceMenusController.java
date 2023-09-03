package com.zelusik.eatery.controller;

import com.zelusik.eatery.dto.place.PlaceMenusDto;
import com.zelusik.eatery.dto.place.request.AddMenuToPlaceMenusRequest;
import com.zelusik.eatery.dto.place.request.PlaceMenusUpdateRequest;
import com.zelusik.eatery.dto.place.response.PlaceMenusResponse;
import com.zelusik.eatery.service.PlaceMenusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static com.zelusik.eatery.constant.ConstantUtil.API_MINOR_VERSION_HEADER_NAME;

@Tag(name = "장소 메뉴 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class PlaceMenusController {

    private final PlaceMenusService placeMenusService;

    @Operation(
            summary = "장소 메뉴 데이터 생성",
            description = "<p><code>placeId</code>에 해당하는 장소의 메뉴 목록을 스크래핑 한 후, 해당 데이터를 DB에 저장한다." +
                    "<p>응답으로, DB에 저장된 메뉴들의 목록을 반환한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "Created", responseCode = "201", content = @Content(schema = @Schema(implementation = PlaceMenusResponse.class))),
            @ApiResponse(description = "[3005] 메뉴 목록 데이터가 이미 존재하는 경우.", responseCode = "409", content = @Content)
    })
    @PostMapping(value = "/v1/places/{placeId}/menus", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public ResponseEntity<PlaceMenusResponse> savePlaceMenusByPlaceIdV1_1(@Parameter(description = "PK of place", example = "3") @PathVariable Long placeId) {
        PlaceMenusDto result = placeMenusService.savePlaceMenus(placeId);
        return ResponseEntity
                .created(URI.create("/api/v1/places/" + placeId + "/menus"))
                .body(PlaceMenusResponse.from(result));
    }

    @Operation(
            summary = "장소 메뉴 데이터 생성",
            description = "<p><code>kakaoPid</code>에 해당하는 장소의 메뉴 목록을 스크래핑 한 후, 해당 데이터를 DB에 저장한다." +
                    "<p>응답으로, DB에 저장된 메뉴들의 목록을 반환한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "Created", responseCode = "201", content = @Content(schema = @Schema(implementation = PlaceMenusResponse.class))),
            @ApiResponse(description = "[3005] 메뉴 목록 데이터가 이미 존재하는 경우.", responseCode = "409", content = @Content)
    })
    @PostMapping(value = "/v1/places/menus", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public ResponseEntity<PlaceMenusResponse> savePlaceMenusByKakaoPidV1_1(@Parameter(description = "장소의 고유 id", example = "1879186093") @RequestParam String kakaoPid) {
        PlaceMenusDto result = placeMenusService.savePlaceMenus(kakaoPid);
        return ResponseEntity
                .created(URI.create("/api/v1/places/" + result.getPlaceId() + "/menus"))
                .body(PlaceMenusResponse.from(result));
    }

    @Operation(
            summary = "장소 메뉴 목록 조회",
            description = "<p><code>placeId</code>에 해당하는 장소의 메뉴 목록 데이터를 조회한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200", content = @Content(schema = @Schema(implementation = PlaceMenusResponse.class))),
            @ApiResponse(description = "[3004] 일치하는 장소의 메뉴 데이터를 찾을 수 없는 경우.", responseCode = "404", content = @Content)
    })
    @GetMapping(value = "/v1/places/{placeId}/menus",headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public PlaceMenusResponse findPlaceMenusByPlaceIdV1_1(@Parameter(description = "PK of place", example = "3") @PathVariable Long placeId) {
        PlaceMenusDto result = placeMenusService.findDtoByPlaceId(placeId);
        return PlaceMenusResponse.fromWithoutIds(result);
    }

    @Operation(
            summary = "장소 메뉴 목록 조회",
            description = "<p><code>kakaoPid</code>에 해당하는 장소의 메뉴 목록 데이터를 조회한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/v1/places/menus", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public PlaceMenusResponse findPlaceMenusByKakaoPidV1_1(@Parameter(description = "장소의 고유 id", example = "1879186093") @RequestParam String kakaoPid) {
        PlaceMenusDto result = placeMenusService.findDtoByKakaoPid(kakaoPid);
        return PlaceMenusResponse.fromWithoutIds(result);
    }

    @Operation(
            summary = "장소 메뉴 목록 업데이트",
            description = "<p>메뉴 목록 데이터를 전달받아 `placeId`에 대해 기존에 존재하는 장소 메뉴 데이터를 업데이트한다." +
                    "<p>전달받은 데이터가 추가되는 것이 아닌, 기존 데이터가 덮어씌워지는 것(overwrite)이므로 주의해야 한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200", content = @Content(schema = @Schema(implementation = PlaceMenusResponse.class))),
            @ApiResponse(description = "[3006] 전달받은 메뉴 목록에 중복된 메뉴가 존재하는 경우", responseCode = "400", content = @Content)
    })
    @PutMapping(value = "/v1/places/{placeId}/menus", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public PlaceMenusResponse updatePlaceMenusV1_1(
            @Parameter(description = "PK of place", example = "3") @PathVariable Long placeId,
            @RequestBody PlaceMenusUpdateRequest request
    ) {
        PlaceMenusDto updatedPlaceMenus = placeMenusService.updateMenus(placeId, request.getMenus());
        return PlaceMenusResponse.from(updatedPlaceMenus);
    }

    @Operation(
            summary = "메뉴 추가하기(직접 등록하기)",
            description = "<p>메뉴를 하나 전달받아 기존 메뉴 목록에 새로 추가한다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200", content = @Content(schema = @Schema(implementation = PlaceMenusResponse.class))),
            @ApiResponse(description = "[3006] 전달받은 메뉴가 기존 메뉴 목록에 이미 존재하는 경우. 즉, 중복된 경우이므로 다른 값으로 요청해야 한다.", responseCode = "400", content = @Content)
    })
    @PatchMapping(value = "/v1/places/{placeId}/menus", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public PlaceMenusResponse addMenuToPlaceMenusV1_1(
            @Parameter(description = "PK of place", example = "3") @PathVariable Long placeId,
            @Valid @RequestBody AddMenuToPlaceMenusRequest request
    ) {
        PlaceMenusDto updatedPlaceMenus = placeMenusService.addMenu(placeId, request.getMenu());
        return PlaceMenusResponse.from(updatedPlaceMenus);
    }

    @Operation(
            summary = "메뉴 목록 데이터 삭제하기 (관리자용)",
            description = "<p>기존에 존재하는 <code>placeId</code>에 해당하는 메뉴 목록 데이터를 전부 삭제한다." +
                    "<p>관리자 권한을 가진 사용자만 사용 가능하다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @DeleteMapping(value = "/v1/places/{placeId}/menus",headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public void deletePlaceMenusV1_1(@Parameter(description = "PK of place", example = "3") @PathVariable Long placeId) {
        placeMenusService.delete(placeId);
    }
}
