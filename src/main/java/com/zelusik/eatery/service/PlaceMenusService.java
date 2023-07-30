package com.zelusik.eatery.service;

import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.place.PlaceMenus;
import com.zelusik.eatery.dto.place.PlaceMenusDto;
import com.zelusik.eatery.exception.place.PlaceMenusNotFoundByPlaceIdException;
import com.zelusik.eatery.repository.place.PlaceMenusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PlaceMenusService {

    private final PlaceService placeService;
    private final PlaceMenusRepository placeMenusRepository;
    private final WebScrapingService webScrapingService;

    /**
     * <p><code>placeId</code>에 해당하는 장소에 대해 메뉴 목록 데이터를 생성 및 저장한다.
     * <p>이 때, 메뉴 목록 데이터는 scraping server에서 받아온다.
     *
     * @param placeId 저장할 메뉴 목록의 장소(PK)
     * @return 저장된 메뉴 목록 정보를 담은 dto
     */
    @NonNull
    @Transactional
    public PlaceMenusDto savePlaceMenus(@NonNull Long placeId) {
        Place place = placeService.findById(placeId);
        List<String> extractedMenus = webScrapingService.scrapMenuList(place.getKakaoPid());
        PlaceMenus placeMenus = placeMenusRepository.save(PlaceMenus.of(place, extractedMenus));
        return PlaceMenusDto.from(placeMenus, placeId);
    }

    /**
     * placeId에 해당하는 장소에 대해 메뉴 목록 데이터를 조회한다.
     *
     * @param placeId 메뉴 데이터를 조회하고자 하는 장소의 PK 값
     * @return 메뉴 목록 정보를 담은 PlaceMenus의 entity 객체
     * @throws PlaceMenusNotFoundByPlaceIdException placeId에 해당하는 장소 메뉴 데이터가 없는 경우
     */
    @NonNull
    private PlaceMenus findByPlaceId(@NonNull Long placeId) {
        return placeMenusRepository.findByPlace_Id(placeId).orElseThrow(() -> new PlaceMenusNotFoundByPlaceIdException(placeId));
    }

    /**
     * placeId에 해당하는 장소에 대해 메뉴 목록 데이터를 조회한다.
     *
     * @param placeId 메뉴 데이터를 조회하고자 하는 장소의 PK 값
     * @return 메뉴 목록 정보를 담은 PlaceMenus의 dto 객체
     * @throws PlaceMenusNotFoundByPlaceIdException placeId에 해당하는 장소 메뉴 데이터가 없는 경우
     */
    @NonNull
    public PlaceMenusDto findDtoByPlaceId(@NonNull Long placeId) {
        return PlaceMenusDto.from(findByPlaceId(placeId), placeId);
    }
}
