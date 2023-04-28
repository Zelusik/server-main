package com.zelusik.eatery.repository.place;

import com.zelusik.eatery.constant.place.DayOfWeek;
import com.zelusik.eatery.constant.place.PlaceSearchKeyword;
import com.zelusik.eatery.dto.place.PlaceDtoWithImages;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PlaceJdbcTemplateRepository {

    /**
     * 중심 좌표 기준, 가까운 순으로 장소 목록을 조회한다.
     *
     * @param daysOfWeek 요일 목록
     * @param keyword    약속 상황
     * @param lat        중심좌표의 위도
     * @param lng        중심좌표의 경도
     * @param pageable   paging 정보
     * @return 조회한 장소 목록
     */
    Slice<PlaceDtoWithImages> findDtosNearBy(Long memberId, List<DayOfWeek> daysOfWeek, PlaceSearchKeyword keyword, String lat, String lng, int distanceLimit, Pageable pageable);

    /**
     * 북마크에 저장한 장소 목록(Slice)을 조회합니다.
     * 정렬 기준은 최근에 저장한 순서입니다.
     *
     * @param memberId 장소를 조회하고자 하는 회원의 PK
     * @param pageable paging 정보
     * @return 조회된 장소 목록(Slice)
     */
    Slice<PlaceDtoWithImages> findMarked(Long memberId, Pageable pageable);
}
