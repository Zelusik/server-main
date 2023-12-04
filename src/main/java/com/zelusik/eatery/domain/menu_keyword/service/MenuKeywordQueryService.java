package com.zelusik.eatery.domain.menu_keyword.service;

import com.zelusik.eatery.domain.menu_keyword.dto.response.MenuKeywordResponse;
import com.zelusik.eatery.domain.menu_keyword.entity.MenuKeyword;
import com.zelusik.eatery.domain.menu_keyword.repository.MenuKeywordRepository;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.review_keyword.constant.MenuKeywordCategory;
import com.zelusik.eatery.global.common.dto.ListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zelusik.eatery.domain.review_keyword.constant.MenuKeywordCategory.MENU_NAME;
import static com.zelusik.eatery.domain.review_keyword.constant.MenuKeywordCategory.PLACE_CATEGORY;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MenuKeywordQueryService {
    private static final int MAX_NUM_OF_RESULTS = 10;

    private final MenuKeywordRepository menuKeywordRepository;

    @NonNull
    public List<MenuKeywordResponse> getKeywords(
            PlaceCategory placeCategory,
            List<String> menus,
            EnumMap<MenuKeywordCategory, List<String>> namesMap,
            List<String> defaultKeywords
    ) {
        // (+ namesMap) 같은 결과가 나오는 중복 query 생성을 방지하기 위해 method 단위로 한 번 조회한 값을 유지한다.
        List<MenuKeywordResponse> result = new ArrayList<>();

        for (String menu : menus) {
            Set<String> keywords = new LinkedHashSet<>(); // 데이터가 쌓인 순서 대로 앞에서 MAX_NUM_OF_RESULTS 만큼 자를 것 이므로 LinkedHashSet 사용

            keywords.addAll(getKeywordsForCategory(namesMap, MENU_NAME, menu));
            if (keywords.size() >= MAX_NUM_OF_RESULTS) {
                result.add(createMenuKeywordResponse(menu, keywords));
                continue;
            }

            String placeCategories = placeCategory.concatAllCategories();
            keywords.addAll(getKeywordsForCategory(namesMap, PLACE_CATEGORY, placeCategories));
            if (keywords.size() >= MAX_NUM_OF_RESULTS) {
                result.add(createMenuKeywordResponse(menu, keywords));
                continue;
            }

            keywords.addAll(defaultKeywords);
            result.add(createMenuKeywordResponse(menu, keywords));
        }
        return result;
    }

    @Cacheable(value = "names", key = "#category")
    public ListDto<String> getNamesForCategory(MenuKeywordCategory category) {
        return new ListDto<>(menuKeywordRepository.getNamesByCategory(category));
    }

    @Cacheable(value = "menu_keywords", key = "'default'")
    public ListDto<String> getDefaultKeywords() {
        return new ListDto<>(menuKeywordRepository.getDefaultMenuKeyword()
                .map(MenuKeyword::getKeywords)
                .orElse(List.of()));
    }

    private List<String> getKeywordsForCategory(EnumMap<MenuKeywordCategory, List<String>> namesMap, MenuKeywordCategory category, String query) {
        List<String> result = new ArrayList<>();
        List<String> filteredNames = namesMap.get(category).stream().filter(query::contains).toList();
        menuKeywordRepository.getAllByNames(filteredNames)
                .forEach(menuKeyword -> result.addAll(menuKeyword.getKeywords()));
        return result;
    }

    private MenuKeywordResponse createMenuKeywordResponse(String menu, Set<String> keywords) {
        return new MenuKeywordResponse(menu, keywords.stream().limit(MAX_NUM_OF_RESULTS).toList());
    }
}
