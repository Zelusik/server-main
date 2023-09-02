package com.zelusik.eatery.repository.place;

import com.zelusik.eatery.constant.FoodCategoryValue;
import com.zelusik.eatery.constant.place.DayOfWeek;
import com.zelusik.eatery.constant.place.FilteringType;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.place.Point;
import com.zelusik.eatery.dto.place.PlaceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.List;

public interface PlaceRepositoryJCustom {

    /**
     * 중심 좌표 기준, 가까운 순으로 장소 목록을 조회한다.
     *
     * @param memberId         API 요청한 회원의 PK 값
     * @param daysOfWeek       요일 목록
     * @param preferredVibe    선호하는 분위기
     * @param center           중심 좌표 정보
     * @param numOfPlaceImages 장소 대표 이미지 최대 개수
     * @param pageable         paging 정보
     * @return 조회한 장소 목록
     */
    Page<PlaceDto> findDtosNearBy(Long memberId, @Nullable FoodCategoryValue foodCategory, @Nullable List<DayOfWeek> daysOfWeek, @Nullable ReviewKeywordValue preferredVibe, Point center, int distanceLimit, int numOfPlaceImages, Pageable pageable);

    /**
     * 북마크에 저장한 장소 목록(Slice)을 조회합니다.
     * 정렬 기준은 최근에 저장한 순서입니다.
     *
     * @param memberId         장소를 조회하고자 하는 회원의 PK
     * @param filteringKeyword filtering keyword
     * @param filteringType    filtering type
     * @param numOfPlaceImages 장소 대표 이미지 최대 개수
     * @param pageable         paging 정보
     * @return 조회된 장소 목록
     */
    Page<PlaceDto> findMarkedPlaces(Long memberId, FilteringType filteringType, String filteringKeyword, int numOfPlaceImages, Pageable pageable);
}
