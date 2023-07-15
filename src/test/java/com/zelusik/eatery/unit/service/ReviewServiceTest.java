package com.zelusik.eatery.unit.service;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.domain.review.ReviewKeyword;
import com.zelusik.eatery.dto.place.PlaceDtoWithMarkedStatus;
import com.zelusik.eatery.dto.review.ReviewDtoWithMember;
import com.zelusik.eatery.dto.review.ReviewDtoWithMemberAndPlace;
import com.zelusik.eatery.dto.review.request.ReviewCreateRequest;
import com.zelusik.eatery.repository.bookmark.BookmarkRepository;
import com.zelusik.eatery.repository.review.ReviewKeywordRepository;
import com.zelusik.eatery.repository.review.ReviewRepository;
import com.zelusik.eatery.exception.review.ReviewDeletePermissionDeniedException;
import com.zelusik.eatery.service.*;
import com.zelusik.eatery.util.MemberTestUtils;
import com.zelusik.eatery.util.MultipartFileTestUtils;
import com.zelusik.eatery.util.PlaceTestUtils;
import com.zelusik.eatery.util.ReviewTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("[Unit] Review Service")
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService sut;

    @Mock
    private ReviewImageService reviewImageService;
    @Mock
    private MemberService memberService;
    @Mock
    private PlaceService placeService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewKeywordRepository reviewKeywordRepository;
    @Mock
    private BookmarkService bookmarkService;

    @DisplayName("생성할 리뷰와 존재하는 장소 정보가 주어지고, 리뷰를 생성하면, 리뷰 생성 후 저장된 리뷰 정보를 반환한다.")
    @Test
    void givenReviewAndExistentPlaceInfo_whenCreateReview_thenReturnSavedReviewInfo() {
        // given
        ReviewCreateRequest reviewCreateRequest = ReviewTestUtils.createReviewCreateRequest();
        String kakaoPid = reviewCreateRequest.getPlace().getKakaoPid();
        long writerId = 1L;
        long placeId = 2L;
        Place expectedPlace = PlaceTestUtils.createPlace(placeId, kakaoPid);
        Member expectedMember = MemberTestUtils.createMember(writerId);
        Review expectedReview = ReviewTestUtils.createReviewWithKeywordsAndImages(3L, expectedMember, expectedPlace);
        given(placeService.findOptByKakaoPid(kakaoPid)).willReturn(Optional.of(expectedPlace));
        given(memberService.findById(writerId)).willReturn(expectedMember);
        given(bookmarkService.isMarkedPlace(writerId, expectedPlace)).willReturn(false);
        given(reviewRepository.save(any(Review.class))).willReturn(expectedReview);
        given(reviewKeywordRepository.save(any(ReviewKeyword.class)))
                .willReturn(ReviewTestUtils.createReviewKeyword(4L, expectedReview, ReviewKeywordValue.FRESH));
        willDoNothing().given(reviewImageService).upload(any(Review.class), any());
        willDoNothing().given(placeService).renewTop3Keywords(expectedPlace);

        // when
        ReviewDtoWithMemberAndPlace actualSavedReview = sut.create(
                writerId,
                reviewCreateRequest,
                List.of(MultipartFileTestUtils.createMockImageDto())
        );

        // then
        then(placeService).should().findOptByKakaoPid(kakaoPid);
        then(memberService).should().findById(writerId);
        then(bookmarkService).should().isMarkedPlace(writerId, expectedPlace);
        then(reviewRepository).should().save(any(Review.class));
        then(reviewKeywordRepository).should().save(any(ReviewKeyword.class));
        then(reviewImageService).should().upload(any(Review.class), any());
        then(placeService).should().renewTop3Keywords(expectedPlace);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualSavedReview.getPlaceDtoWithMarkedStatus().getKakaoPid()).isEqualTo(kakaoPid);
    }

    @DisplayName("생성할 리뷰와 존재하지 않는 장소 정보가 주어지고, 리뷰를 생성하면, 장소와 리뷰 생성 후 저장된 리뷰 정보를 반환한다.")
    @Test
    void givenReviewAndNotExistentPlaceInfo_whenCreateReview_thenSavePlaceAndReview() {
        // given
        long writerId = 1L;
        long placeId = 2L;
        ReviewCreateRequest reviewCreateRequest = ReviewTestUtils.createReviewCreateRequest();
        String kakaoPid = reviewCreateRequest.getPlace().getKakaoPid();
        Place expectedPlace = PlaceTestUtils.createPlace(placeId, kakaoPid);
        PlaceDtoWithMarkedStatus expectedPlaceDtoWithMarkedStatus = PlaceDtoWithMarkedStatus.from(expectedPlace, true);
        Member expectedMember = MemberTestUtils.createMember(writerId);
        Review expectedReview = ReviewTestUtils.createReviewWithKeywordsAndImages(3L, expectedMember, expectedPlace);
        given(placeService.findOptByKakaoPid(kakaoPid)).willReturn(Optional.empty());
        given(placeService.create(writerId, reviewCreateRequest.getPlace())).willReturn(expectedPlaceDtoWithMarkedStatus);
        given(placeService.findById(placeId)).willReturn(expectedPlace);
        given(memberService.findById(writerId)).willReturn(expectedMember);
        given(bookmarkService.isMarkedPlace(writerId, expectedPlace)).willReturn(false);
        given(reviewRepository.save(any(Review.class))).willReturn(expectedReview);
        given(reviewKeywordRepository.save(any(ReviewKeyword.class))).willReturn(ReviewTestUtils.createReviewKeyword(4L, expectedReview, ReviewKeywordValue.FRESH));
        willDoNothing().given(reviewImageService).upload(any(Review.class), any());
        willDoNothing().given(placeService).renewTop3Keywords(expectedPlace);

        // when
        ReviewDtoWithMemberAndPlace actualSavedReview = sut.create(
                writerId,
                reviewCreateRequest,
                List.of(MultipartFileTestUtils.createMockImageDto())
        );

        // then
        then(placeService).should().findOptByKakaoPid(kakaoPid);
        then(placeService).should().create(writerId, reviewCreateRequest.getPlace());
        then(placeService).should().findById(placeId);
        then(memberService).should().findById(writerId);
        then(bookmarkService).should().isMarkedPlace(writerId, expectedPlace);
        then(reviewRepository).should().save(any(Review.class));
        then(reviewKeywordRepository).should().save(any(ReviewKeyword.class));
        then(reviewImageService).should().upload(any(Review.class), any());
        then(placeService).should().renewTop3Keywords(expectedPlace);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualSavedReview.getPlaceDtoWithMarkedStatus().getKakaoPid()).isEqualTo(kakaoPid);
    }

    @DisplayName("가게의 id(PK)가 주어지고, 특정 가게에 대한 리뷰 목록을 조회하면, 조회된 리뷰 목록(Slice)을 반환한다.")
    @Test
    void givenPlaceId_whenSearchReviewListOfCertainPlace_thenReturnReviewList() {
        // given
        long placeId = 3L;
        Pageable pageable = Pageable.ofSize(15);
        SliceImpl<Review> expectedSearchResult = new SliceImpl<>(List.of(ReviewTestUtils.createReview(1L, 2L, placeId, "3", 4L, 5L)));
        given(reviewRepository.findByPlace_IdAndDeletedAtNull(placeId, pageable))
                .willReturn(expectedSearchResult);

        // when
        Slice<ReviewDtoWithMember> actualSearchResult = sut.findDtosByPlaceId(placeId, pageable);

        // then
        then(reviewRepository).should().findByPlace_IdAndDeletedAtNull(placeId, pageable);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualSearchResult.hasContent()).isTrue();
    }

    @DisplayName("회원의 PK가 주어지고, 해당 회원이 작성한 리뷰 목록을 조회하면, 조회된 리뷰 목록(Slice)을 반환한다.")
    @Test
    void givenMemberId_whenSearchReviewsOfWriter_thenReturnReviews() {
        // given
        long writerId = 2L;
        Pageable pageable = Pageable.ofSize(15);
        SliceImpl<Review> expectedSearchResult = new SliceImpl<>(List.of(ReviewTestUtils.createReview(1L, writerId, 3L, "3", 4L, 5L)));
        given(reviewRepository.findByWriter_IdAndDeletedAtNull(writerId, pageable)).willReturn(expectedSearchResult);
        given(bookmarkService.isMarkedPlace(eq(writerId), any(Place.class))).willReturn(false);

        // when
        Slice<ReviewDtoWithMemberAndPlace> actualSearchResult = sut.findDtosByWriterId(writerId, pageable);

        // then
        then(reviewRepository).should().findByWriter_IdAndDeletedAtNull(writerId, pageable);
        then(bookmarkService).should().isMarkedPlace(eq(writerId), any(Place.class));
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualSearchResult.hasContent()).isTrue();
    }

    @DisplayName("리뷰를 삭제하면, 리뷰와 모든 리뷰 이미지들을 soft delete하고 모든 리뷰 키워드들을 삭제한다.")
    @Test
    void given_whenSoftDeleteReview_thenSoftDeleteReviewAndAllReviewImagesAndDeleteAllReviewKeywords() {
        // given
        long memberId = 1L;
        long reviewId = 3L;
        Member loginMember = MemberTestUtils.createMember(memberId);
        Place place = PlaceTestUtils.createPlace(2L, "2");
        Review findReview = ReviewTestUtils.createReviewWithKeywordsAndImages(reviewId, loginMember, place);
        given(memberService.findById(memberId)).willReturn(loginMember);
        given(reviewRepository.findByIdAndDeletedAtNull(reviewId)).willReturn(Optional.of(findReview));
        willDoNothing().given(reviewImageService).softDeleteAll(findReview.getReviewImages());
        willDoNothing().given(reviewKeywordRepository).deleteAll(findReview.getKeywords());
        willDoNothing().given(reviewRepository).flush();
        willDoNothing().given(placeService).renewTop3Keywords(findReview.getPlace());

        // when
        sut.delete(memberId, reviewId);

        // then
        then(memberService).should().findById(memberId);
        then(reviewRepository).should().findByIdAndDeletedAtNull(reviewId);
        then(reviewImageService).should().softDeleteAll(findReview.getReviewImages());
        then(reviewKeywordRepository).should().deleteAll(findReview.getKeywords());
        then(reviewRepository).should().flush();
        then(placeService).should().renewTop3Keywords(findReview.getPlace());
        verifyEveryMocksShouldHaveNoMoreInteractions();
    }

    @DisplayName("리뷰 삭제 권한이 없는 회원의 PK가 주어지고, 리뷰를 soft delete하면, 예외가 발생한다.")
    @Test
    void givenUnauthorizedMemberId_whenSoftDeleteReview_thenThrowException() {
        // given
        long loginMemberId = 1L;
        long reviewWriterId = 2L;
        long reviewId = 3L;
        Place place = PlaceTestUtils.createPlace(4L, "2");
        Member loginMember = MemberTestUtils.createMember(loginMemberId);
        Member reviewWriter = MemberTestUtils.createMember(reviewWriterId);
        Review findReview = ReviewTestUtils.createReviewWithKeywordsAndImages(reviewId, reviewWriter, place);
        given(memberService.findById(loginMemberId)).willReturn(loginMember);
        given(reviewRepository.findByIdAndDeletedAtNull(reviewId)).willReturn(Optional.of(findReview));

        // when
        Throwable t = catchThrowable(() -> sut.delete(loginMemberId, reviewId));

        // then
        then(memberService).should().findById(loginMemberId);
        then(reviewRepository).should().findByIdAndDeletedAtNull(reviewId);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(t).isInstanceOf(ReviewDeletePermissionDeniedException.class);
    }

    private void verifyEveryMocksShouldHaveNoMoreInteractions() {
        then(reviewImageService).shouldHaveNoMoreInteractions();
        then(memberService).shouldHaveNoMoreInteractions();
        then(placeService).shouldHaveNoMoreInteractions();
        then(reviewRepository).shouldHaveNoMoreInteractions();
        then(reviewKeywordRepository).shouldHaveNoMoreInteractions();
        then(bookmarkService).shouldHaveNoMoreInteractions();
    }
}