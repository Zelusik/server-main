package com.zelusik.eatery.domain.place.repository;

import com.zelusik.eatery.domain.place.constant.FilteringType;
import com.zelusik.eatery.domain.place.dto.PlaceWithMarkedStatusAndImagesDto;
import com.zelusik.eatery.domain.place.dto.request.FindNearPlacesFilteringConditionRequest;
import com.zelusik.eatery.domain.place.entity.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlaceRepositoryJCustom {

    /**
     * 중심 좌표 기준, 가까운 순으로 장소 목록을 조회한다.
     * 영업 시간 정보는 함께 조회하지 않는다.
     *
     * @param loginMemberId      PK of login member
     * @param filteringCondition 필터링 조건
     * @param center             중심 좌표 정보
     * @param distanceLimit      조회할 주변 장소에 대한 거리 제한. 이 값보다 멀리 있는 장소는 조회 대상에서 제외한다.
     * @param numOfPlaceImages   장소 대표 이미지 최대 개수
     * @param pageable           paging 정보
     * @return 조회한 장소 목록
     */
    Page<PlaceWithMarkedStatusAndImagesDto> findDtosWithoutOpeningHoursNearBy(long loginMemberId, FindNearPlacesFilteringConditionRequest filteringCondition, Point center, int distanceLimit, int numOfPlaceImages, Pageable pageable);

    /**
     * 북마크에 저장한 장소 목록(Slice)을 조회합니다.
     * 정렬 기준은 최근에 저장한 순서이다.
     * 영업 시간 정보는 함께 조회하지 않는다.
     *
     * @param memberId         장소를 조회하고자 하는 회원의 PK
     * @param filteringKeyword filtering keyword
     * @param filteringType    filtering type
     * @param numOfPlaceImages 장소 대표 이미지 최대 개수
     * @param pageable         paging 정보
     * @return 조회된 장소 목록
     */
    Page<PlaceWithMarkedStatusAndImagesDto> findMarkedDtosWithoutOpeningHours(Long memberId, FilteringType filteringType, String filteringKeyword, int numOfPlaceImages, Pageable pageable);
}
