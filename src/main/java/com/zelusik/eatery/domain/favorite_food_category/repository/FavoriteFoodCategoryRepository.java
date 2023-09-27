package com.zelusik.eatery.domain.favorite_food_category.repository;

import com.zelusik.eatery.domain.favorite_food_category.entity.FavoriteFoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteFoodCategoryRepository extends JpaRepository<FavoriteFoodCategory, Long> {
}
