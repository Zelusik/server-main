package com.zelusik.eatery.repository.place;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.dto.place.PlaceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;

import static com.zelusik.eatery.domain.QBookmark.bookmark;
import static com.zelusik.eatery.domain.place.QPlace.place;

@RequiredArgsConstructor
public class PlaceRepositoryQCustomImpl implements PlaceRepositoryQCustom {

    private final JPAQueryFactory queryFactory;

    public Optional<PlaceDto> findDtoWithMarkedStatus(Long id, Long memberId) {
        Place result = queryFactory.selectFrom(place)
                .where(place.id.eq(id))
                .fetchOne();

        if (result == null) {
            return Optional.empty();
        }

        Long count = queryFactory.select(bookmark.count())
                .from(bookmark)
                .where(bookmark.place.id.eq(id)
                        .and(bookmark.member.id.eq(memberId)))
                .fetchOne();
        boolean isMarked = count != null && count > 0;

        return Optional.of(PlaceDto.from(result, isMarked));
    }

    @Override
    public Slice<Place> searchByKeyword(String keyword, Pageable pageable) {
        List<Place> content = queryFactory
                .selectFrom(place)
                .where(place.name.containsIgnoreCase(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)  // 다음 페이지 존재 여부 확인을 위함
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
