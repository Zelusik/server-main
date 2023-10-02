package com.zelusik.eatery.domain.place.service;

import com.zelusik.eatery.domain.opening_hours.entity.OpeningHours;
import com.zelusik.eatery.domain.opening_hours.repository.OpeningHoursRepository;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.place.dto.request.PlaceCreateRequest;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.exception.PlaceAlreadyExistsException;
import com.zelusik.eatery.domain.place.repository.PlaceRepository;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review_keyword.repository.ReviewKeywordRepository;
import com.zelusik.eatery.global.scraping.dto.KakaoPlaceScrapingInfo;
import com.zelusik.eatery.global.scraping.exception.ScrapingServerInternalError;
import com.zelusik.eatery.global.scraping.service.WebScrapingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class PlaceCommandService {

    private final WebScrapingService webScrapingService;
    private final PlaceRepository placeRepository;
    private final OpeningHoursRepository openingHoursRepository;
    private final ReviewKeywordRepository reviewKeywordRepository;

    /**
     * 장소 정보를 받아 장소를 저장한다.
     *
     * @param placeCreateRequest 장소 정보가 담긴 dto.
     * @return 저장된 장소 entity.
     * @throws ScrapingServerInternalError Web scraping 서버에서 에러가 발생한 경우
     * @throws PlaceAlreadyExistsException 동일한 장소 데이터가 이미 존재하는 경우
     */
    public PlaceDto create(PlaceCreateRequest placeCreateRequest) {
        String kakaoPid = placeCreateRequest.getKakaoPid();
        if (placeRepository.existsByKakaoPid(kakaoPid)) {
            throw new PlaceAlreadyExistsException(kakaoPid);
        }
        KakaoPlaceScrapingInfo scrapingInfo = webScrapingService.getPlaceScrapingInfo(placeCreateRequest.getKakaoPid());
        Place savedPlace = placeRepository.save(placeCreateRequest.toDto(scrapingInfo.getHomepageUrl(), scrapingInfo.getClosingHours()).toEntity());

        if (scrapingInfo.getOpeningHours() == null || scrapingInfo.getOpeningHours().isEmpty()) {
            return PlaceDto.from(savedPlace);
        }

        List<OpeningHours> openingHoursList = scrapingInfo.getOpeningHours().stream()
                .map(oh -> oh.toOpeningHoursEntity(savedPlace))
                .toList();
        openingHoursRepository.saveAll(openingHoursList);
        savedPlace.getOpeningHoursList().addAll(openingHoursList);
        return PlaceDto.from(savedPlace);
    }

    /**
     * 장소의 top 3 keyword를 DB에서 조회 후 갱신한다.
     *
     * @param place top 3 keyword를 갱신할 장소
     */
    public void renewTop3Keywords(Place place) {
        List<ReviewKeywordValue> placeTop3Keywords = reviewKeywordRepository.searchTop3Keywords(place.getId());
        place.setTop3Keywords(placeTop3Keywords);
    }
}
