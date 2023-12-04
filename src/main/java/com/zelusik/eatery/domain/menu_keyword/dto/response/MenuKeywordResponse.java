package com.zelusik.eatery.domain.menu_keyword.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MenuKeywordResponse {

    @Schema(description = "메뉴 이름", example = "버터치킨카레")
    private String menu;

    @Schema(description = "메뉴에 해당하는 키워드 목록 (최대 10개)", example = "[\"단짠\", \"부드러운\", \"촉촉한\", \"짭조름한\", \"노릇노릇\"]")
    private List<String> keywords;
}
