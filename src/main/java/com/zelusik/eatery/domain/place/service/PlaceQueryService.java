package com.zelusik.eatery.domain.place.service;

import com.zelusik.eatery.domain.bookmark.service.BookmarkQueryService;
import com.zelusik.eatery.domain.place.constant.FilteringType;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.place.dto.PlaceFilteringKeywordDto;
import com.zelusik.eatery.domain.place.dto.PlaceWithMarkedStatusAndImagesDto;
import com.zelusik.eatery.domain.place.dto.request.FindNearPlacesFilteringConditionRequest;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.place.exception.PlaceNotFoundByKakaoPidException;
import com.zelusik.eatery.domain.place.exception.PlaceNotFoundException;
import com.zelusik.eatery.domain.place.repository.PlaceRepository;
import com.zelusik.eatery.domain.review_image.dto.ReviewImageDto;
import com.zelusik.eatery.domain.review_image.service.ReviewImageQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PlaceQueryService {

    public static final int MAX_NUM_OF_PLACE_IMAGES = 4;
    public static final int DISTANCE_LIMITS_FOR_FIND_NEARBY_PLACES = 50;

    private final ReviewImageQueryService reviewImageQueryService;
    private final PlaceRepository placeRepository;
    private final BookmarkQueryService bookmarkQueryService;

    /**
     * kakaoPid에 해당하는 장소를 조회한 후 반환한다.
     *
     * @param kakaoPid 조회하고자 하는 장소의 kakaoPid
     * @return 조회한 장소의 optional entity
     */
    public Optional<Place> findByKakaoPid(String kakaoPid) {
        return placeRepository.findByKakaoPid(kakaoPid);
    }

    /**
     * placeId에 해당하는 장소를 조회한 후 반환한다.
     *
     * @param placeId 조회하고자 하는 장소의 PK
     * @return 조회한 장소 entity
     * @throws PlaceNotFoundException 조회하고자 하는 장소가 존재하지 않는 경우
     */
    public Place getById(Long placeId) {
        return placeRepository.findById(placeId).orElseThrow(PlaceNotFoundException::new);
    }

    /**
     * <code>kakaoPid</code>에 해당하는 장소를 조회한다.
     *
     * @param kakaoPid 조회하고자 하는 장소의 kakao unique id
     * @return 조회한 장소 entity
     * @throws PlaceNotFoundException 조회하고자 하는 장소가 존재하지 않는 경우
     */
    @NonNull
    public Place getByKakaoPid(@NonNull String kakaoPid) {
        return findByKakaoPid(kakaoPid).orElseThrow(() -> new PlaceNotFoundByKakaoPidException(kakaoPid));
    }

    /**
     * placeId에 해당하는 장소를 조회한 후 반환한다.
     *
     * @param placeId 조회하고자 하는 장소의 PK
     * @return 조회한 장소 dto
     */
    public PlaceWithMarkedStatusAndImagesDto getDtoWithMarkedStatusAndImagesById(Long memberId, Long placeId) {
        Place foundPlace = getById(placeId);
        boolean isMarked = bookmarkQueryService.isMarkedPlace(memberId, foundPlace);
        List<ReviewImageDto> latest3Images = reviewImageQueryService.findLatest3ByPlace(foundPlace.getId());
        return PlaceWithMarkedStatusAndImagesDto.from(foundPlace, isMarked, latest3Images);
    }

    /**
     * <code>kakaoPid</code>에 해당하는 장소를 조회한다.
     *
     * @param kakaoPid 조회하고자 하는 장소의 kakao unique id
     * @return 조회한 장소 dto
     */
    @NonNull
    public PlaceWithMarkedStatusAndImagesDto getDtoWithMarkedStatusAndImagesByKakaoPid(@NonNull Long memberId, @NonNull String kakaoPid) {
        Place foundPlace = getByKakaoPid(kakaoPid);
        boolean isMarked = bookmarkQueryService.isMarkedPlace(memberId, foundPlace);
        List<ReviewImageDto> latest3ByPlace = reviewImageQueryService.findLatest3ByPlace(foundPlace.getId());
        return PlaceWithMarkedStatusAndImagesDto.from(foundPlace, isMarked, latest3ByPlace);
    }

    /**
     * 검색 키워드를 받아 장소를 검색한다.
     *
     * @param searchKeyword 검색 키워드
     * @param pageable      paging 정보
     * @return 조회된 장소 목록
     */
    public Slice<PlaceDto> searchDtosByKeyword(String searchKeyword, Pageable pageable) {
        return placeRepository.searchByKeyword(searchKeyword, pageable).map(PlaceDto::from);
    }

    /**
     * <p>중심 좌표 기준, 가까운 순으로 장소 목록을 검색한다.
     * <p>영업 시간 정보는 함께 조회하지 않는다.
     * <p>최대 50km 범위까지 조회한다.
     *
     * @param loginMemberId      PK of login member
     * @param filteringCondition 필터링 조건
     * @param center             중심 좌표 정보
     * @param pageable           paging 정보
     * @return 조회한 장소 목록
     */
    public Page<PlaceWithMarkedStatusAndImagesDto> findDtosWithoutOpeningHoursNearBy(long loginMemberId, FindNearPlacesFilteringConditionRequest filteringCondition, Point center, Pageable pageable) {
        return placeRepository.findDtosWithoutOpeningHoursNearBy(loginMemberId, filteringCondition, center, DISTANCE_LIMITS_FOR_FIND_NEARBY_PLACES, MAX_NUM_OF_PLACE_IMAGES, pageable);
    }

    /**
     * <p>북마크에 저장한 장소 목록(Slice)을 조회한다.
     * <p>영업 시간 정보는 함께 조회하지 않는다.
     * <p>조회 시 장소와 관련된 이미지를 함께 조회한다.
     * <p>이미지를 가져오는 기준은 "해당 장소에 작성된 리뷰에 담긴 사진 중 최신순으로 최대 3개"입니다.
     *
     * @param memberId 로그인 회원의 PK.
     * @param pageable paging 정보
     * @return 조회한 장소 목록
     */
    public Page<PlaceWithMarkedStatusAndImagesDto> findMarkedDtosWithoutOpeningHours(Long memberId, FilteringType filteringType, String filteringKeyword, Pageable pageable) {
        return placeRepository.findMarkedDtosWithoutOpeningHours(memberId, filteringType, filteringKeyword, MAX_NUM_OF_PLACE_IMAGES, pageable);
    }

    /**
     * {@code memberId}에 해당하는 회원의 저장한 장소들에 대해 filtering keyword 목록을 조회한다.
     *
     * @param memberId filtering keywords를 조회하고자 하는 회원의 PK
     * @return 조회된 filtering keywords
     */
    public List<PlaceFilteringKeywordDto> getFilteringKeywords(Long memberId) {
        return placeRepository.getFilteringKeywords(memberId);
    }

    /**
     * <code>kakaoPid</code>로 장소 존재 여부를 조회한다.
     *
     * @param kakaoPid Kakao place unique id
     * @return 장소 존재 여부
     */
    public boolean existsByKakaoPid(String kakaoPid) {
        return placeRepository.existsByKakaoPid(kakaoPid);
    }
}
