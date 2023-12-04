package com.zelusik.eatery.domain.place_menus.service;

import com.zelusik.eatery.domain.place_menus.dto.PlaceMenusDto;
import com.zelusik.eatery.domain.place_menus.entity.PlaceMenus;
import com.zelusik.eatery.domain.place_menus.exception.PlaceMenusNotFoundByKakaoPidException;
import com.zelusik.eatery.domain.place_menus.exception.PlaceMenusNotFoundByPlaceIdException;
import com.zelusik.eatery.domain.place_menus.repository.PlaceMenusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PlaceMenusQueryService {

    private final PlaceMenusRepository placeMenusRepository;

    /**
     * placeId에 해당하는 장소에 대해 메뉴 목록 데이터를 조회한다.
     *
     * @param placeId 메뉴 데이터를 조회하고자 하는 장소의 PK 값
     * @return 메뉴 목록 정보를 담은 PlaceMenus의 dto 객체
     * @throws PlaceMenusNotFoundByPlaceIdException placeId에 해당하는 장소 메뉴 데이터가 없는 경우
     */
    @NonNull
    public PlaceMenusDto getDtoByPlaceId(@NonNull Long placeId) {
        PlaceMenus placeMenus = placeMenusRepository.findByPlace_Id(placeId).orElseThrow(() -> new PlaceMenusNotFoundByPlaceIdException(placeId));
        return PlaceMenusDto.from(placeMenus);
    }

    /**
     * <p><code>kakaoPid</code>에 해당하는 장소에 대해 메뉴 목록 데이터를 조회한다.
     *
     * @param kakaoPid 메뉴 데이터를 조회하고자 하는 장소의 고유 id 값
     * @return 메뉴 목록 정보를 담은 PlaceMenus의 dto 객체
     * @throws PlaceMenusNotFoundByPlaceIdException <code>kakaoPid</code>에 해당하는 장소 메뉴 데이터가 없는 경우
     */
    @NonNull
    public PlaceMenusDto getDtoByKakaoPid(@NonNull String kakaoPid) {
        PlaceMenus placeMenus = placeMenusRepository.findByPlace_KakaoPid(kakaoPid)
                .orElseThrow(() -> new PlaceMenusNotFoundByKakaoPidException(kakaoPid));
        return PlaceMenusDto.from(placeMenus);
    }
}
