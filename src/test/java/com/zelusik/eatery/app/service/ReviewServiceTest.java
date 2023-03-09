package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.Member;
import com.zelusik.eatery.app.domain.Review;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.dto.place.PlaceScrapingInfo;
import com.zelusik.eatery.app.dto.review.ReviewDto;
import com.zelusik.eatery.app.dto.review.request.ReviewCreateRequest;
import com.zelusik.eatery.app.repository.ReviewRepository;
import com.zelusik.eatery.util.MemberTestUtils;
import com.zelusik.eatery.util.PlaceTestUtils;
import com.zelusik.eatery.util.ReviewTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Service] Review")
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService sut;

    @Mock
    private WebScrapingService webScrapingService;
    @Mock
    private MemberService memberService;
    @Mock
    private PlaceService placeService;
    @Mock
    private ReviewRepository reviewRepository;

    @DisplayName("생성할 리뷰와 존재하는 장소 정보가 주어지고, 리뷰를 생성하면, 리뷰 생성 후 저장된 리뷰 정보를 반환한다.")
    @Test
    void givenReviewAndExistentPlaceInfo_whenCreateReview_thenReturnSavedReviewInfo() {
        // given
        ReviewCreateRequest reviewCreateRequest = ReviewTestUtils.createReviewCreateRequest();
        String kakaoPid = reviewCreateRequest.getPlace().getKakaoPid();
        long uploaderId = 1L;
        Place expectedPlace = PlaceTestUtils.createPlaceWithId();
        Member expectedMember = MemberTestUtils.createMemberWithId();
        given(placeService.findOptEntityByKakaoPid(kakaoPid)).willReturn(Optional.of(expectedPlace));
        given(memberService.findEntityById(uploaderId)).willReturn(expectedMember);
        given(reviewRepository.save(any(Review.class))).willReturn(ReviewTestUtils.createReviewWithId(expectedMember, expectedPlace));

        // when
        ReviewDto actualSavedReview = sut.create(uploaderId, reviewCreateRequest);

        // then
        then(placeService).should().findOptEntityByKakaoPid(kakaoPid);
        then(memberService).should().findEntityById(uploaderId);
        then(reviewRepository).should().save(any(Review.class));
        assertThat(actualSavedReview.placeDto().kakaoPid()).isEqualTo(kakaoPid);
    }

    @DisplayName("생성할 리뷰와 존재하지 않는 장소 정보가 주어지고, 리뷰를 생성하면, 리뷰 생성 후 저장된 리뷰 정보를 반환한다.")
    @Test
    void givenReviewAndNotExistentPlaceInfo_whenCreateReview_thenReturnSavedReviewInfo() {
        // given
        ReviewCreateRequest reviewCreateRequest = ReviewTestUtils.createReviewCreateRequest();
        String kakaoPid = reviewCreateRequest.getPlace().getKakaoPid();
        long uploaderId = 1L;
        String homepageUrl = "homepage";
        String openingHours = "매일 00:00 ~ 24:00";
        String closingHours = null;
        Place expectedPlace = PlaceTestUtils.createPlaceWithId();
        Member expectedMember = MemberTestUtils.createMemberWithId();
        given(placeService.findOptEntityByKakaoPid(kakaoPid))
                .willReturn(Optional.empty());
        given(webScrapingService.getPlaceScrapingInfo(reviewCreateRequest.getPlace().getPageUrl()))
                .willReturn(new PlaceScrapingInfo(openingHours, closingHours, homepageUrl));
        given(placeService.create(reviewCreateRequest.getPlace(), homepageUrl, openingHours, closingHours))
                .willReturn(expectedPlace);
        given(memberService.findEntityById(uploaderId))
                .willReturn(expectedMember);
        given(reviewRepository.save(any(Review.class)))
                .willReturn(ReviewTestUtils.createReviewWithId(expectedMember, expectedPlace));

        // when
        ReviewDto actualSavedReview = sut.create(uploaderId, reviewCreateRequest);

        // then
        then(placeService).should().findOptEntityByKakaoPid(kakaoPid);
        then(webScrapingService).should().getPlaceScrapingInfo(reviewCreateRequest.getPlace().getPageUrl());
        then(placeService).should().create(reviewCreateRequest.getPlace(), homepageUrl, openingHours, closingHours);
        then(memberService).should().findEntityById(uploaderId);
        then(reviewRepository).should().save(any(Review.class));
        assertThat(actualSavedReview.placeDto().kakaoPid()).isEqualTo(kakaoPid);
    }
}