package com.zelusik.eatery.repository.menu_keyword;

import com.zelusik.eatery.constant.MenuKeywordCategory;
import com.zelusik.eatery.domain.MenuKeyword;

import java.util.List;
import java.util.Optional;

public interface MenuKeywordRepositoryQCustom {

    Optional<MenuKeyword> getDefaultMenuKeyword();

    List<String> getNamesByCategory(MenuKeywordCategory category);

    List<MenuKeyword> getAllByNames(List<String> names);
}
