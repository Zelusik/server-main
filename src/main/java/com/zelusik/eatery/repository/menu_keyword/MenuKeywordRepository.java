package com.zelusik.eatery.repository.menu_keyword;

import com.zelusik.eatery.domain.MenuKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuKeywordRepository extends JpaRepository<MenuKeyword, Long>, MenuKeywordRepositoryQCustom {
}
