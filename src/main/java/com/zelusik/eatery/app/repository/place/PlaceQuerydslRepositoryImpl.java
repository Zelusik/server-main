package com.zelusik.eatery.app.repository.place;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zelusik.eatery.app.domain.place.Place;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.zelusik.eatery.app.domain.QBookmark.bookmark;
import static com.zelusik.eatery.app.domain.member.QMember.member;
import static com.zelusik.eatery.app.domain.place.QPlace.place;

@RequiredArgsConstructor
public class PlaceQuerydslRepositoryImpl implements PlaceQuerydslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Place> findMarkedPlaces(Long memberId, Pageable pageable) {
        List<Place> content = queryFactory.select(place)
                .from(bookmark)
                .join(bookmark.member, member)
                .join(bookmark.place, place)
                .where(member.id.eq(memberId))
                .orderBy(bookmark.createdAt.desc())
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
