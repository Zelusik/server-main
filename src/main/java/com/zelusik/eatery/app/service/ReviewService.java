package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.Member;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.dto.place.PlaceScrapingInfo;
import com.zelusik.eatery.app.dto.place.request.PlaceRequest;
import com.zelusik.eatery.app.dto.review.ReviewDto;
import com.zelusik.eatery.app.dto.review.request.ReviewCreateRequest;
import com.zelusik.eatery.app.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewService {

    private final WebScrapingService webScrapingService;
    private final MemberService memberService;
    private final PlaceService placeService;
    private final ReviewRepository reviewRepository;

    /**
     * 리뷰를 생성합니다.
     *
     * @param uploaderId 리뷰를 생성하고자 하는 회원의 PK.
     * @param reviewRequest 생성할 리뷰의 정보. 여기에 장소 정보도 포함되어 있다.
     * @return 생성된 리뷰 정보가 담긴 dto.
     */
    @Transactional
    public ReviewDto create(Long uploaderId, ReviewCreateRequest reviewRequest) {
        PlaceRequest placeRequest = reviewRequest.getPlace();
        Place place = placeService.findOptEntityByKakaoPid(placeRequest.getKakaoPid())
                .orElseGet(() -> {
                    PlaceScrapingInfo scrapingInfo = webScrapingService.getPlaceScrapingInfo(placeRequest.getPageUrl());
                    return placeService.create(
                            placeRequest,
                            scrapingInfo.homepageUrl(),
                            scrapingInfo.openingHours(),
                            scrapingInfo.closingHours()
                    );
                });

        Member uploader = memberService.findEntityById(uploaderId);

        ReviewDto reviewDto = reviewRequest.toDto(place);
        return ReviewDto.from(reviewRepository.save(reviewDto.toEntity(uploader, place)));
    }
}