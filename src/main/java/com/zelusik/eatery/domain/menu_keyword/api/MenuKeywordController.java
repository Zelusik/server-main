package com.zelusik.eatery.domain.menu_keyword.api;

import com.zelusik.eatery.domain.review_keyword.constant.MenuKeywordCategory;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.menu_keyword.dto.response.MenuKeywordListResponseList;
import com.zelusik.eatery.domain.menu_keyword.dto.response.MenuKeywordResponse;
import com.zelusik.eatery.domain.menu_keyword.service.MenuKeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.zelusik.eatery.global.common.constant.ConstantUtil.API_MINOR_VERSION_HEADER_NAME;
import static com.zelusik.eatery.domain.review_keyword.constant.MenuKeywordCategory.MENU_NAME;
import static com.zelusik.eatery.domain.review_keyword.constant.MenuKeywordCategory.PLACE_CATEGORY;

@Tag(name = "메뉴 키워드(맛, 평가, 느낌 등) 관련 API")
@RequestMapping("/api")
@Validated
@RequiredArgsConstructor
@RestController
public class MenuKeywordController {

    private final MenuKeywordService menuKeywordService;

    @Operation(
            summary = "메뉴 키워드 조회",
            description = "<p><strong>Latest version: v1.1</strong>" +
                          "<p>각 메뉴에 대해 적절한 키워드 목록을 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping(value = "/v1/menu-keywords", headers = API_MINOR_VERSION_HEADER_NAME + "=1")
    public MenuKeywordListResponseList getMenuKeywordsV1_1(
            @Parameter(
                    description = "<p>리뷰를 작성하고자 하는 장소의 카테고리 정보. Kakao에서 전달받은 정보 그대로 사용한다.",
                    example = "음식점 > 한식 > 육류,고기 > 삼겹살"
            ) @RequestParam @NotBlank String placeCategory,
            @Parameter(
                    description = "키워드를 조회하고자 하는 메뉴 목록 (메뉴 이름에 쉼표(<code>,</code>가 있으면 안됨)",
                    example = "[\"시금치 카츠 카레\", \"버터치킨카레\"]"
            ) @RequestParam List<@NotBlank String> menus
    ) {
        EnumMap<MenuKeywordCategory, List<String>> namesMap = new EnumMap<>(Map.of(
                MENU_NAME, menuKeywordService.getNamesForCategory(MENU_NAME).getContent(),
                PLACE_CATEGORY, menuKeywordService.getNamesForCategory(PLACE_CATEGORY).getContent()
        ));
        List<String> defaultKeywords = menuKeywordService.getDefaultKeywords().getContent();

        List<MenuKeywordResponse> result = menuKeywordService.getKeywords(PlaceCategory.of(placeCategory), menus, namesMap, defaultKeywords);
        return new MenuKeywordListResponseList(result);
    }
}