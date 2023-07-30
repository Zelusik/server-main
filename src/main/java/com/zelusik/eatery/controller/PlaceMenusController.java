package com.zelusik.eatery.controller;

import com.zelusik.eatery.dto.place.PlaceMenusDto;
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

import java.net.URI;

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
    @PostMapping("/places/{placeId}/menus")
    public ResponseEntity<PlaceMenusResponse> savePlaceMenus(@Parameter(description = "PK of place", example = "3") @PathVariable Long placeId) {
        PlaceMenusDto result = placeMenusService.savePlaceMenus(placeId);
        return ResponseEntity
                .created(URI.create("/api/places/" + placeId + "/menus"))
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
    @GetMapping("/places/{placeId}/menus")
    public PlaceMenusResponse findPlaceMenus(@Parameter(description = "PK of place", example = "3") @PathVariable Long placeId) {
        PlaceMenusDto result = placeMenusService.findDtoByPlaceId(placeId);
        return PlaceMenusResponse.fromWithoutIds(result);
    }
}
