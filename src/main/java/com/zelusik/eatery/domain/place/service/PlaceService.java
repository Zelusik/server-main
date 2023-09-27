package com.zelusik.eatery.domain.place.service;

import com.zelusik.eatery.domain.place.constant.FilteringType;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.bookmark.service.BookmarkService;
import com.zelusik.eatery.domain.opening_hours.entity.OpeningHours;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.place.dto.PlaceFilteringKeywordDto;
import com.zelusik.eatery.domain.place.dto.PlaceScrapingInfo;
import com.zelusik.eatery.domain.place.dto.request.FindNearPlacesFilteringConditionRequest;
import com.zelusik.eatery.domain.place.dto.request.PlaceCreateRequest;
import com.zelusik.eatery.domain.review_image.dto.ReviewImageDto;
import com.zelusik.eatery.domain.place.exception.PlaceAlreadyExistsException;
import com.zelusik.eatery.domain.place.exception.PlaceNotFoundException;
import com.zelusik.eatery.global.scraping.exception.ScrapingServerInternalError;
import com.zelusik.eatery.domain.opening_hours.repository.OpeningHoursRepository;
import com.zelusik.eatery.domain.place.repository.PlaceRepository;
import com.zelusik.eatery.domain.review_keyword.repository.ReviewKeywordRepository;
import com.zelusik.eatery.domain.review_image.service.ReviewImageService;
import com.zelusik.eatery.global.scraping.service.WebScrapingService;
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
public class PlaceService {

    public static final int MAX_NUM_OF_PLACE_IMAGES = 4;
    public static final int DISTANCE_LIMITS_FOR_FIND_NEARBY_PLACES = 50;

    private final WebScrapingService webScrapingService;
    private final ReviewImageService reviewImageService;
    private final PlaceRepository placeRepository;
    private final OpeningHoursRepository openingHoursRepository;
    private final BookmarkService bookmarkService;
    private final ReviewKeywordRepository reviewKeywordRepository;

    /**
     * 장소 정보를 받아 장소를 저장한다.
     *
     * @param placeCreateRequest 장소 정보가 담긴 dto.
     * @return 저장된 장소 entity.
     * @throws ScrapingServerInternalError Web scraping 서버에서 에러가 발생한 경우
     * @throws PlaceAlreadyExistsException 동일한 장소 데이터가 이미 존재하는 경우
     */
    @Transactional
    public PlaceDto create(Long memberId, PlaceCreateRequest placeCreateRequest) {
        String kakaoPid = placeCreateRequest.getKakaoPid();
        if (placeRepository.existsByKakaoPid(kakaoPid)) {
            throw new PlaceAlreadyExistsException(kakaoPid);
        }

        PlaceScrapingInfo scrapingInfo = webScrapingService.getPlaceScrapingInfo(placeCreateRequest.getKakaoPid());

        Place savedPlace = placeRepository.save(placeCreateRequest.toDto(scrapingInfo.getHomepageUrl(), scrapingInfo.getClosingHours()).toEntity());
        boolean placeMarkedStatus = bookmarkService.isMarkedPlace(memberId, savedPlace);

        if (scrapingInfo.getOpeningHours() == null || scrapingInfo.getOpeningHours().isEmpty()) {
            return PlaceDto.from(savedPlace, placeMarkedStatus);
        }

        List<OpeningHours> openingHoursList = scrapingInfo.getOpeningHours().stream()
                .map(oh -> oh.toOpeningHoursEntity(savedPlace))
                .toList();
        openingHoursRepository.saveAll(openingHoursList);
        savedPlace.getOpeningHoursList().addAll(openingHoursList);
        return PlaceDto.from(savedPlace, placeMarkedStatus);
    }

    /**
     * kakaoPid에 해당하는 장소를 조회한 후 반환한다.
     *
     * @param kakaoPid 조회하고자 하는 장소의 kakaoPid
     * @return 조회한 장소의 optional entity
     */
    public Optional<Place> findOptByKakaoPid(String kakaoPid) {
        return placeRepository.findByKakaoPid(kakaoPid);
    }

    /**
     * placeId에 해당하는 장소를 조회한 후 반환한다.
     *
     * @param placeId 조회하고자 하는 장소의 PK
     * @return 조회한 장소 entity
     * @throws PlaceNotFoundException 조회하고자 하는 장소가 존재하지 않는 경우
     */
    public Place findById(Long placeId) {
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
    public Place findByKakaoPid(@NonNull String kakaoPid) {
        return findOptByKakaoPid(kakaoPid).orElseThrow(() -> PlaceNotFoundException.kakaoPid(kakaoPid));
    }

    /**
     * placeId에 해당하는 장소를 조회한 후 반환한다.
     *
     * @param placeId 조회하고자 하는 장소의 PK
     * @return 조회한 장소 dto
     */
    public PlaceDto findDtoWithMarkedStatusAndImagesById(Long memberId, Long placeId) {
        Place foundPlace = findById(placeId);
        boolean isMarked = bookmarkService.isMarkedPlace(memberId, foundPlace);
        List<ReviewImageDto> latest3Images = reviewImageService.findLatest3ByPlace(foundPlace.getId());
        return PlaceDto.fromWithImages(foundPlace, isMarked, latest3Images);
    }

    /**
     * <code>kakaoPid</code>에 해당하는 장소를 조회한다.
     *
     * @param kakaoPid 조회하고자 하는 장소의 kakao unique id
     * @return 조회한 장소 dto
     */
    @NonNull
    public PlaceDto findDtoWithMarkedStatusAndImagesByKakaoPid(@NonNull Long memberId, @NonNull String kakaoPid) {
        Place foundPlace = findByKakaoPid(kakaoPid);
        boolean isMarked = bookmarkService.isMarkedPlace(memberId, foundPlace);
        List<ReviewImageDto> latest3ByPlace = reviewImageService.findLatest3ByPlace(foundPlace.getId());
        return PlaceDto.fromWithImages(foundPlace, isMarked, latest3ByPlace);
    }

    /**
     * 검색 키워드를 받아 장소를 검색한다.
     *
     * @param searchKeyword 검색 키워드
     * @param pageable      paging 정보
     * @return 조회된 장소 목록
     */
    public Slice<PlaceDto> searchDtosByKeyword(String searchKeyword, Pageable pageable) {
        return placeRepository.searchByKeyword(searchKeyword, pageable).map(PlaceDto::fromWithoutMarkedStatusAndImages);
    }

    /**
     * <p>중심 좌표 기준, 가까운 순으로 장소 목록을 검색한다.
     * <p>최대 50km 범위까지 조회한다.
     *
     * @param loginMemberId      PK of login member
     * @param filteringCondition 필터링 조건
     * @param center             중심 좌표 정보
     * @param pageable           paging 정보
     * @return 조회한 장소 목록
     */
    public Page<PlaceDto> findDtosNearBy(long loginMemberId, FindNearPlacesFilteringConditionRequest filteringCondition, Point center, Pageable pageable) {
        return placeRepository.findDtosNearBy(loginMemberId, filteringCondition, center, DISTANCE_LIMITS_FOR_FIND_NEARBY_PLACES, MAX_NUM_OF_PLACE_IMAGES, pageable);
    }

    /**
     * 북마크에 저장한 장소 목록(Slice)을 조회합니다.
     * 조회 시 장소와 관련된 이미지를 함께 조회합니다.
     * 이미지를 가져오는 기준은 "해당 장소에 작성된 리뷰에 담긴 사진 중 최신순으로 최대 3개"입니다.
     *
     * @param memberId 로그인 회원의 PK.
     * @param pageable paging 정보
     * @return 조회한 장소 목록
     */
    public Page<PlaceDto> findMarkedDtos(Long memberId, FilteringType filteringType, String filteringKeyword, Pageable pageable) {
        return placeRepository.findMarkedPlaces(memberId, filteringType, filteringKeyword, MAX_NUM_OF_PLACE_IMAGES, pageable);
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

    /**
     * 장소의 top 3 keyword를 DB에서 조회 후 갱신한다.
     *
     * @param place top 3 keyword를 갱신할 장소
     */
    @Transactional
    public void renewTop3Keywords(Place place) {
        List<ReviewKeywordValue> placeTop3Keywords = reviewKeywordRepository.searchTop3Keywords(place.getId());
        place.setTop3Keywords(placeTop3Keywords);
    }
}
