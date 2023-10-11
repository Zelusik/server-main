package com.zelusik.eatery.domain.place_menus.service;

import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.exception.ContainsDuplicateMenusException;
import com.zelusik.eatery.domain.place_menus.exception.PlaceMenusAlreadyExistsException;
import com.zelusik.eatery.domain.place_menus.exception.PlaceMenusNotFoundByPlaceIdException;
import com.zelusik.eatery.domain.place.service.PlaceQueryService;
import com.zelusik.eatery.domain.place_menus.dto.PlaceMenusDto;
import com.zelusik.eatery.domain.place_menus.entity.PlaceMenus;
import com.zelusik.eatery.domain.place_menus.repository.PlaceMenusRepository;
import com.zelusik.eatery.global.scraping.service.WebScrapingService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class PlaceMenusCommandService {

    private final PlaceQueryService placeQueryService;
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
    public PlaceMenusDto savePlaceMenus(@NonNull Long placeId) {
        if (placeMenusRepository.existsByPlace_Id(placeId)) {
            throw new PlaceMenusAlreadyExistsException(placeId);
        }
        Place place = placeQueryService.findById(placeId);
        return savePlaceMenus(place);
    }

    /**
     * <p><code>kakaoPid</code>에 해당하는 장소에 대해 메뉴 목록 데이터를 생성 및 저장한다.
     * <p>이 때, 메뉴 목록 데이터는 scraping server에서 받아온다.
     *
     * @param kakaoPid 저장할 메뉴 목록의 장소에 대한 고유 id(for kakao)
     * @return 저장된 메뉴 목록 정보를 담은 dto
     */
    @NonNull
    public PlaceMenusDto savePlaceMenus(@NonNull String kakaoPid) {
        if (placeMenusRepository.existsByPlace_KakaoPid(kakaoPid)) {
            throw new PlaceMenusAlreadyExistsException(kakaoPid);
        }
        Place place = placeQueryService.findByKakaoPid(kakaoPid);
        return savePlaceMenus(place);
    }

    @NonNull
    private PlaceMenusDto savePlaceMenus(Place place) {
        List<String> extractedMenus = webScrapingService.scrapMenuList(place.getKakaoPid());
        PlaceMenus placeMenus = placeMenusRepository.save(PlaceMenus.of(place, extractedMenus));
        return PlaceMenusDto.from(placeMenus);
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
     * <code>placeId</code>에 해당하는 장소 메뉴 entity의 메뉴 목록 데이터(<code>menus</code>)를 업데이트(overwrite)한다.
     *
     * @param placeId 메뉴 데이터를 업데이트하고자 하는 장소의 PK 값
     * @param menus   덮어쓰기하려는 메뉴 목록 데이터
     * @return 업데이트된 장소 메뉴 dto 객체
     * @throws ContainsDuplicateMenusException 전달받은 메뉴 목록에 중복된 메뉴가 존재할 경우
     */
    @NonNull
    public PlaceMenusDto updateMenus(@NonNull Long placeId, @Nullable List<String> menus) {
        if (menus != null && checkContainsDuplicateNonWhiteSpaceStrings(menus)) {
            throw new ContainsDuplicateMenusException(placeId, menus);
        }

        PlaceMenus placeMenus = findByPlaceId(placeId);
        placeMenus.updateMenus(menus);
        return PlaceMenusDto.from(placeMenus);
    }

    /**
     * <code>placeId</code>에 해당하는 장소 메뉴 목록에 전달받은 <code>menu</code>를 추가한다.
     *
     * @param placeId 메뉴를 추가하고자 하는 장소의 PK 값
     * @param menu    추가하려는 메뉴
     * @return 업데이트된 장소 메뉴 dto 객체
     * @throws ContainsDuplicateMenusException 전달받은 메뉴가 기존 메뉴 목록 데이터에 존재하는 경우
     */
    @NonNull
    public PlaceMenusDto addMenu(@NonNull Long placeId, @NotBlank String menu) {
        PlaceMenus placeMenus = findByPlaceId(placeId);
        placeMenus.addMenu(menu);
        if (checkContainsDuplicateNonWhiteSpaceStrings(placeMenus.getMenus())) {
            throw new ContainsDuplicateMenusException(placeId, menu);
        }
        return PlaceMenusDto.from(placeMenus);
    }

    /**
     * 공백을 제거했을 때를 기준으로, 전달받은 리스트에 동일한 문자열이 존재하는지 확인한다.
     *
     * @param list 동일한 문자열이 존재하는지 확인하고자 하는 리스트
     * @return 동일한 문자열이 존재하는지 여부
     */
    private boolean checkContainsDuplicateNonWhiteSpaceStrings(List<String> list) {
        List<String> trimmedList = list.stream().map(s -> s.replaceAll("\s+", "")).toList();
        Set<String> set = new HashSet<>(trimmedList);
        return list.size() != set.size();
    }

    public void delete(@NonNull Long placeId) {
        placeMenusRepository.deleteByPlace_Id(placeId);
    }
}
