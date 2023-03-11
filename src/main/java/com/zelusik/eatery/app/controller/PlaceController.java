package com.zelusik.eatery.app.controller;

import com.zelusik.eatery.app.dto.place.response.PlaceResponse;
import com.zelusik.eatery.app.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/places")
@RestController
public class PlaceController {

    private final PlaceService placeService;

    @Operation(
            summary = "가게 단건 조회",
            description = "가게의 PK를 받아 해당하는 가게 정보를 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @ApiResponses({
            @ApiResponse(description = "OK", responseCode = "200", content = @Content(schema = @Schema(implementation = PlaceResponse.class))),
            @ApiResponse(description = "3001: 찾고자 하는 가게가 존재하지 않는 경우", responseCode = "404", content = @Content)
    })
    @GetMapping("/{placeId}")
    public PlaceResponse find(@PathVariable Long placeId) {
        return PlaceResponse.from(placeService.findDtoById(placeId));
    }
}
