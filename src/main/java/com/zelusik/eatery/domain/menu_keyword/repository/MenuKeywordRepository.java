package com.zelusik.eatery.domain.menu_keyword.repository;

import com.zelusik.eatery.domain.menu_keyword.entity.MenuKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuKeywordRepository extends JpaRepository<MenuKeyword, Long>, MenuKeywordRepositoryQCustom {
}
