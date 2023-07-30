package com.zelusik.eatery.dto.place.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AddMenuToPlaceMenusRequest {

    @Schema(description = "새로 추가할 메뉴 이름", example = "양념치킨")
    @NotBlank
    private String menu;
}
