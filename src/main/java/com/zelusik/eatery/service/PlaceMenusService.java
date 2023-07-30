package com.zelusik.eatery.service;

import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.place.PlaceMenus;
import com.zelusik.eatery.dto.place.PlaceMenusDto;
import com.zelusik.eatery.exception.place.ContainsDuplicateMenusException;
import com.zelusik.eatery.exception.place.PlaceMenusAlreadyExistsException;
import com.zelusik.eatery.exception.place.PlaceMenusNotFoundException;
import com.zelusik.eatery.repository.place.PlaceMenusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        if (placeMenusRepository.existsByPlace_Id(placeId)) {
            throw new PlaceMenusAlreadyExistsException(placeId);
        }

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
     * @throws PlaceMenusNotFoundException placeId에 해당하는 장소 메뉴 데이터가 없는 경우
     */
    @NonNull
    private PlaceMenus findByPlaceId(@NonNull Long placeId) {
        return placeMenusRepository.findByPlace_Id(placeId).orElseThrow(() -> new PlaceMenusNotFoundException(placeId));
    }

    /**
     * <p><code>kakaoPid</code>에 해당하는 장소에 대해 메뉴 목록 데이터를 조회한다.
     *
     * @param kakaoPid 메뉴 데이터를 조회하고자 하는 장소의 고유 id 값
     * @return 메뉴 목록 정보를 담은 PlaceMenus의 entity 객체
     * @throws PlaceMenusNotFoundException <code>kakaoPid</code>에 해당하는 장소 메뉴 데이터가 없는 경우
     */
    @NonNull
    private PlaceMenus findByKakaoPid(@NonNull String kakaoPid) {
        return placeMenusRepository.findByPlace_KakaoPid(kakaoPid).orElseThrow(() -> new PlaceMenusNotFoundException(kakaoPid));
    }

    /**
     * placeId에 해당하는 장소에 대해 메뉴 목록 데이터를 조회한다.
     *
     * @param placeId 메뉴 데이터를 조회하고자 하는 장소의 PK 값
     * @return 메뉴 목록 정보를 담은 PlaceMenus의 dto 객체
     * @throws PlaceMenusNotFoundException placeId에 해당하는 장소 메뉴 데이터가 없는 경우
     */
    @NonNull
    public PlaceMenusDto findDtoByPlaceId(@NonNull Long placeId) {
        return PlaceMenusDto.from(findByPlaceId(placeId), placeId);
    }

    /**
     * <p><code>kakaoPid</code>에 해당하는 장소에 대해 메뉴 목록 데이터를 조회한다.
     *
     * @param kakaoPid 메뉴 데이터를 조회하고자 하는 장소의 고유 id 값
     * @return 메뉴 목록 정보를 담은 PlaceMenus의 dto 객체
     * @throws PlaceMenusNotFoundException <code>kakaoPid</code>에 해당하는 장소 메뉴 데이터가 없는 경우
     */
    @NonNull
    public PlaceMenusDto findDtoByKakaoPid(@NonNull String kakaoPid) {
        return PlaceMenusDto.from(findByKakaoPid(kakaoPid));
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
    @Transactional
    public PlaceMenusDto updateMenus(@NonNull Long placeId, @Nullable List<String> menus) {
        if (menus != null && checkContainsDuplicateNonWhiteSpaceStrings(menus)) {
            throw new ContainsDuplicateMenusException(menus);
        }

        PlaceMenus placeMenus = findByPlaceId(placeId);
        placeMenus.updateMenus(menus);
        return PlaceMenusDto.from(placeMenus);
    }

    /**
     * 공백을 제거했을 때를 기준으로, 전달받은 리스트에 동일한 문자열이 존재하는지 확인한다.
     *
     * @param list 동일한 문자열이 존재하는지 확인하고자 하는 리스트
     * @return 동일한 문자열이 존재하는지 여부
     */
    private boolean checkContainsDuplicateNonWhiteSpaceStrings(List<String> list) {
        List<String> trimmedList = list.stream().map(s -> s.replace(" ", "")).toList();
        Set<String> set = new HashSet<>(trimmedList);
        return list.size() != set.size();
    }
}
