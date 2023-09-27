package com.zelusik.eatery.domain.menu_keyword.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zelusik.eatery.domain.review_keyword.constant.MenuKeywordCategory;
import com.zelusik.eatery.domain.menu_keyword.entity.MenuKeyword;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.zelusik.eatery.domain.menu_keyword.entity.QMenuKeyword.menuKeyword;

@RequiredArgsConstructor
public class MenuKeywordRepositoryQCustomImpl implements MenuKeywordRepositoryQCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<MenuKeyword> getDefaultMenuKeyword() {
        return Optional.ofNullable(
                queryFactory.selectFrom(menuKeyword)
                        .where(menuKeyword.category.eq(MenuKeywordCategory.DEFAULT))
                        .fetchOne()
        );
    }

    @Override
    public List<String> getNamesByCategory(MenuKeywordCategory category) {
        return queryFactory.select(menuKeyword.name)
                .from(menuKeyword)
                .where(menuKeyword.category.eq(category))
                .fetch();
    }

    @Override
    public List<MenuKeyword> getAllByNames(List<String> names) {
        return queryFactory.selectFrom(menuKeyword)
                .where(menuKeyword.name.in(names))
                .fetch();
    }
}
