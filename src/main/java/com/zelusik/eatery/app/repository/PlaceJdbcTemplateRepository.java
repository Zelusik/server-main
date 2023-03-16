package com.zelusik.eatery.app.repository;

import com.zelusik.eatery.app.domain.place.Place;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PlaceJdbcTemplateRepository {

    /**
     * 중심 좌표 기준, 가까운 순으로 장소 목록을 조회한다.
     *
     * @param lat      중심좌표의 위도
     * @param lng      중심좌표의 경도
     * @param pageable paging 정보
     * @return 조회한 장소 목록
     */
    Slice<Place> findNearBy(String lat, String lng, int distanceLimit, Pageable pageable);
}
