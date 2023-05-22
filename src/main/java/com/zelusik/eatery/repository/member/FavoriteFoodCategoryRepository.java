package com.zelusik.eatery.repository.member;

import com.zelusik.eatery.domain.member.FavoriteFoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteFoodCategoryRepository extends JpaRepository<FavoriteFoodCategory, Long> {
}
