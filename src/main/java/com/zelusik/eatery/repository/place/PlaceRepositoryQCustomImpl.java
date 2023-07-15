package com.zelusik.eatery.repository.place;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.place.QPlace;
import com.zelusik.eatery.dto.place.PlaceDto;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.zelusik.eatery.domain.QBookmark.bookmark;

@RequiredArgsConstructor
public class PlaceRepositoryQCustomImpl implements PlaceRepositoryQCustom {

    private final JPAQueryFactory queryFactory;

    public Optional<PlaceDto> findDtoWithMarkedStatus(Long id, Long memberId) {
        Place place = queryFactory.selectFrom(QPlace.place)
                .where(QPlace.place.id.eq(id))
                .fetchOne();

        if (place == null) {
            return Optional.empty();
        }

        Long count = queryFactory.select(bookmark.count())
                .from(bookmark)
                .where(bookmark.place.id.eq(id)
                        .and(bookmark.member.id.eq(memberId)))
                .fetchOne();
        boolean isMarked = count != null && count > 0;

        return Optional.of(PlaceDto.from(place, isMarked));
    }
}
