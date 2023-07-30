package com.zelusik.eatery.controller;

import com.zelusik.eatery.dto.place.PlaceMenusDto;
import com.zelusik.eatery.dto.place.response.PlaceMenusResponse;
import com.zelusik.eatery.service.PlaceMenusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<PlaceMenusResponse> savePlaceMenuList(@Parameter(description = "PK of place", example = "3") @PathVariable Long placeId) {
        PlaceMenusDto result = placeMenusService.savePlaceMenuList(placeId);
        return ResponseEntity
                .created(URI.create("/api/places/" + placeId + "/menus"))
                .body(PlaceMenusResponse.from(result));
    }
}
