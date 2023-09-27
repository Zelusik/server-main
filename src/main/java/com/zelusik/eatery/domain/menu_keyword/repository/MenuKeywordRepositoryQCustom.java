package com.zelusik.eatery.domain.menu_keyword.repository;

import com.zelusik.eatery.domain.review_keyword.constant.MenuKeywordCategory;
import com.zelusik.eatery.domain.menu_keyword.entity.MenuKeyword;

import java.util.List;
import java.util.Optional;

public interface MenuKeywordRepositoryQCustom {

    Optional<MenuKeyword> getDefaultMenuKeyword();

    List<String> getNamesByCategory(MenuKeywordCategory category);

    List<MenuKeyword> getAllByNames(List<String> names);
}
