package com.zelusik.eatery.controller;

import com.zelusik.eatery.constant.MenuKeywordCategory;
import com.zelusik.eatery.domain.place.PlaceCategory;
import com.zelusik.eatery.dto.menu_keyword.response.MenuKeywordListResponseList;
import com.zelusik.eatery.dto.menu_keyword.response.MenuKeywordResponse;
import com.zelusik.eatery.service.MenuKeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

import static com.zelusik.eatery.constant.MenuKeywordCategory.MENU_NAME;
import static com.zelusik.eatery.constant.MenuKeywordCategory.PLACE_CATEGORY;

@RequestMapping("/api/menu-keywords")
@Validated
@RequiredArgsConstructor
@RestController
public class MenuKeywordController {

    private final MenuKeywordService menuKeywordService;

    @Operation(
            summary = "메뉴 키워드 조회",
            description = "각 메뉴에 대해 적절한 키워드 목록을 조회합니다.",
            security = @SecurityRequirement(name = "access-token")
    )
    @GetMapping
    public MenuKeywordListResponseList getMenuKeywords(
            @Parameter(
                    description = "<p>리뷰를 작성하고자 하는 장소의 카테고리 중 첫 번째 카테고리. " +
                            "<p>\"한식 > 육류,고기 > 삼겹살\" 중 \"한식\"에 해당한다.",
                    example = "한식"
            ) @RequestParam @NotBlank String firstCategory,
            @Parameter(
                    description = "<p>리뷰를 작성하고자 하는 장소의 카테고리 중 두 번째 카테고리. " +
                            "<p>\"한식 > 육류,고기 > 삼겹살\" 중 \"육류,고기\"에 해당한다.",
                    example = "육류,고기"
            ) @RequestParam(required = false) String secondCategory,
            @Parameter(
                    description = "<p>리뷰를 작성하고자 하는 장소의 카테고리 중 세 번째 카테고리. " +
                            "<p>\"한식 > 육류,고기 > 삼겹살\" 중 \"삼겹살\"에 해당한다.",
                    example = "삼겹살"
            ) @RequestParam(required = false) String thirdCategory,
            @Parameter(
                    description = "키워드를 조회하고자 하는 메뉴 목록",
                    example = "[\"시금치 카츠 카레\", \"버터치킨카레\"]"
            ) @RequestParam List<@NotBlank String> menus
    ) {
        EnumMap<MenuKeywordCategory, List<String>> namesMap = new EnumMap<>(Map.of(
                MENU_NAME, menuKeywordService.getNamesForCategory(MENU_NAME).getContent(),
                PLACE_CATEGORY, menuKeywordService.getNamesForCategory(PLACE_CATEGORY).getContent()
        ));
        List<String> defaultKeywords = menuKeywordService.getDefaultKeywords().getContent();

        List<MenuKeywordResponse> result = menuKeywordService.getKeywords(
                new PlaceCategory(firstCategory, secondCategory, thirdCategory),
                menus,
                namesMap,
                defaultKeywords
        );
        return new MenuKeywordListResponseList(result);
    }
}
