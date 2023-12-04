package com.zelusik.eatery.domain.place_menus.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PlaceMenusUpdateRequest {

    @Schema(description = "업데이트할 메뉴 목록", example = "[\"후라이드 치킨\", \"양념 치킨\", \"치킨무\"]")
    private List<String> menus;
}
