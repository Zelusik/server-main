package com.zelusik.eatery.domain.location.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zelusik.eatery.domain.location.entity.Location;
import com.zelusik.eatery.domain.location.dto.LocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.zelusik.eatery.domain.location.entity.QLocation.location;

@RequiredArgsConstructor
public class LocationRepositoryQCustomImpl implements LocationRepositoryQCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<LocationDto> searchDtosByKeyword(String keyword, Pageable pageable) {
        List<Location> fetchResult = queryFactory.selectFrom(location)
                .where(location.sido.like("%" + keyword + "%")
                        .or(location.sgg.like("%" + keyword + "%"))
                        .or(location.emdg.like("%" + keyword + "%")))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
        List<LocationDto> content = fetchResult.stream()
                .map(LocationDto::from)
                .toList();

        Long count = queryFactory.select(location.count())
                .from(location)
                .where(location.sido.like("%" + keyword + "%")
                        .or(location.sgg.like("%" + keyword + "%"))
                        .or(location.emdg.like("%" + keyword + "%")))
                .fetchOne();
        assert count != null;

        return new PageImpl<>(content, pageable, count);
    }
}
