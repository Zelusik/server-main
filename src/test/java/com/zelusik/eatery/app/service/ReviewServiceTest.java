package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.domain.Review;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.dto.review.ReviewDtoWithMember;
import com.zelusik.eatery.app.dto.review.ReviewDtoWithMemberAndPlace;
import com.zelusik.eatery.app.dto.review.request.ReviewCreateRequest;
import com.zelusik.eatery.app.repository.bookmark.BookmarkRepository;
import com.zelusik.eatery.app.repository.review.ReviewRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("[Service] Review")
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService sut;

    @Mock
    private ReviewFileService reviewFileService;
    @Mock
    private MemberService memberService;
    @Mock
    private PlaceService placeService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private BookmarkRepository bookmarkRepository;

    @DisplayName("생성할 리뷰와 존재하는 장소 정보가 주어지고, 리뷰를 생성하면, 리뷰 생성 후 저장된 리뷰 정보를 반환한다.")
    @Test
    void givenReviewAndExistentPlaceInfo_whenCreateReview_thenReturnSavedReviewInfo() {
        // given
        ReviewCreateRequest reviewCreateRequest = ReviewTestUtils.createReviewCreateRequest();
        String kakaoPid = reviewCreateRequest.getPlace().getKakaoPid();
        long writerId = 1L;
        Place expectedPlace = PlaceTestUtils.createPlace();
        Member expectedMember = MemberTestUtils.createMember(writerId);
        given(placeService.findOptEntityByKakaoPid(kakaoPid)).willReturn(Optional.of(expectedPlace));
        given(memberService.findEntityById(writerId)).willReturn(expectedMember);
        given(reviewRepository.save(any(Review.class))).willReturn(ReviewTestUtils.createReviewWithId(expectedMember, expectedPlace));
        willDoNothing().given(reviewFileService).upload(any(Review.class), any());

        // when
        ReviewDtoWithMemberAndPlace actualSavedReview = sut.create(
                writerId,
                reviewCreateRequest,
                List.of(MultipartFileTestUtils.createMockMultipartFile())
        );

        // then
        then(placeService).should().findOptEntityByKakaoPid(kakaoPid);
        then(memberService).should().findEntityById(writerId);
        then(reviewRepository).should().save(any(Review.class));
        then(reviewFileService).should().upload(any(Review.class), any());
        assertThat(actualSavedReview.placeDto().kakaoPid()).isEqualTo(kakaoPid);
    }

    @DisplayName("생성할 리뷰와 존재하지 않는 장소 정보가 주어지고, 리뷰를 생성하면, 장소와 리뷰 생성 후 저장된 리뷰 정보를 반환한다.")
    @Test
    void givenReviewAndNotExistentPlaceInfo_whenCreateReview_thenSavePlaceAndReview() {
        // given
        ReviewCreateRequest reviewCreateRequest = ReviewTestUtils.createReviewCreateRequest();
        String kakaoPid = reviewCreateRequest.getPlace().getKakaoPid();
        long writerId = 1L;
        Place expectedPlace = PlaceTestUtils.createPlace();
        Member expectedMember = MemberTestUtils.createMember(writerId);
        given(placeService.findOptEntityByKakaoPid(kakaoPid))
                .willReturn(Optional.empty());
        given(placeService.create(reviewCreateRequest.getPlace()))
                .willReturn(expectedPlace);
        given(memberService.findEntityById(writerId))
                .willReturn(expectedMember);
        given(reviewRepository.save(any(Review.class)))
                .willReturn(ReviewTestUtils.createReviewWithId(expectedMember, expectedPlace));
        willDoNothing().given(reviewFileService).upload(any(Review.class), any());
        given(bookmarkRepository.findAllMarkedPlaceId(writerId)).willReturn(List.of());

        // when
        ReviewDtoWithMemberAndPlace actualSavedReview = sut.create(
                writerId,
                reviewCreateRequest,
                List.of(MultipartFileTestUtils.createMockMultipartFile())
        );

        // then
        then(placeService).should().findOptEntityByKakaoPid(kakaoPid);
        then(placeService).should().create(reviewCreateRequest.getPlace());
        then(memberService).should().findEntityById(writerId);
        then(reviewRepository).should().save(any(Review.class));
        then(reviewFileService).should().upload(any(Review.class), any());
        then(bookmarkRepository).should().findAllMarkedPlaceId(writerId);
        assertThat(actualSavedReview.placeDto().kakaoPid()).isEqualTo(kakaoPid);
    }

    @DisplayName("가게의 id(PK)가 주어지고, 특정 가게에 대한 리뷰 목록을 조회하면, 조회된 리뷰 목록(Slice)을 반환한다.")
    @Test
    void givenPlaceId_whenSearchReviewListOfCertainPlace_thenReturnReviewList() {
        // given
        long placeId = 1L;
        Pageable pageable = Pageable.ofSize(15);
        SliceImpl<Review> expectedSearchResult = new SliceImpl<>(List.of(ReviewTestUtils.createReviewWithId()));
        given(reviewRepository.findByPlace_IdAndDeletedAtNull(placeId, pageable))
                .willReturn(expectedSearchResult);

        // when
        Slice<ReviewDtoWithMember> actualSearchResult = sut.searchDtosByPlaceId(placeId, pageable);

        // then
        then(reviewRepository).should().findByPlace_IdAndDeletedAtNull(placeId, pageable);
        assertThat(actualSearchResult.hasContent()).isTrue();
    }

    @DisplayName("회원의 PK가 주어지고, 해당 회원이 작성한 리뷰 목록을 조회하면, 조회된 리뷰 목록(Slice)을 반환한다.")
    @Test
    void givenMemberId_whenSearchReviewsOfWriter_thenReturnReviews() {
        // given
        long writerId = 1L;
        Pageable pageable = Pageable.ofSize(15);
        SliceImpl<Review> expectedSearchResult = new SliceImpl<>(List.of(ReviewTestUtils.createReviewWithId()));
        given(reviewRepository.findByWriter_IdAndDeletedAtNull(writerId, pageable)).willReturn(expectedSearchResult);
        given(bookmarkRepository.findAllMarkedPlaceId(writerId)).willReturn(List.of());

        // when
        Slice<ReviewDtoWithMemberAndPlace> actualSearchResult = sut.searchDtosByWriterId(writerId, pageable);

        // then
        then(reviewRepository).should().findByWriter_IdAndDeletedAtNull(writerId, pageable);
        then(bookmarkRepository).should().findAllMarkedPlaceId(writerId);
        assertThat(actualSearchResult.hasContent()).isTrue();
    }
}