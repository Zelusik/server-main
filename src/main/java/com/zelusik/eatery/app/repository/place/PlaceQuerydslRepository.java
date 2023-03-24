package com.zelusik.eatery.app.repository.place;

import com.zelusik.eatery.app.domain.place.Place;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PlaceQuerydslRepository {

    /**
     * 북마크에 저장한 장소 목록(Slice)을 조회합니다.
     * 정렬 기준은 최근에 저장한 순서입니다.
     *
     * @param memberId 장소를 조회하고자 하는 회원의 PK
     * @param pageable paging 정보
     * @return 조회된 장소 목록(Slice)
     */
    Slice<Place> findMarkedPlaces(Long memberId, Pageable pageable);
}
