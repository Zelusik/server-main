package com.zelusik.eatery.unit.domain.review.service;

import com.zelusik.eatery.domain.bookmark.service.BookmarkQueryService;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.member.service.MemberQueryService;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.place.service.PlaceCommandService;
import com.zelusik.eatery.domain.place.service.PlaceQueryService;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review.dto.ReviewDto;
import com.zelusik.eatery.domain.review.dto.request.ReviewCreateRequest;
import com.zelusik.eatery.domain.review.entity.Review;
import com.zelusik.eatery.domain.review.exception.ReviewDeletePermissionDeniedException;
import com.zelusik.eatery.domain.review.repository.ReviewRepository;
import com.zelusik.eatery.domain.review.service.ReviewCommandService;
import com.zelusik.eatery.domain.review.service.ReviewQueryService;
import com.zelusik.eatery.domain.review_image.dto.request.ReviewImageCreateRequest;
import com.zelusik.eatery.domain.review_image.entity.ReviewImage;
import com.zelusik.eatery.domain.review_image.service.ReviewImageService;
import com.zelusik.eatery.domain.review_image_menu_tag.dto.request.MenuTagPointCreateRequest;
import com.zelusik.eatery.domain.review_image_menu_tag.dto.request.ReviewMenuTagCreateRequest;
import com.zelusik.eatery.domain.review_image_menu_tag.repository.ReviewImageMenuTagRepository;
import com.zelusik.eatery.domain.review_keyword.entity.ReviewKeyword;
import com.zelusik.eatery.domain.review_keyword.repository.ReviewKeywordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("[Unit] Service(Command) - Review")
@ExtendWith(MockitoExtension.class)
class ReviewCommandServiceTest {

    @InjectMocks
    private ReviewCommandService sut;

    @Mock
    private ReviewQueryService reviewQueryService;
    @Mock
    private ReviewImageService reviewImageService;
    @Mock
    private MemberQueryService memberQueryService;
    @Mock
    private PlaceCommandService placeCommandService;
    @Mock
    private PlaceQueryService placeQueryService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewKeywordRepository reviewKeywordRepository;
    @Mock
    private BookmarkQueryService bookmarkQueryService;
    @Mock
    private ReviewImageMenuTagRepository reviewImageMenuTagRepository;

    @DisplayName("생성할 리뷰와 존재하는 장소 정보가 주어지고, 리뷰를 생성하면, 리뷰 생성 후 저장된 리뷰 정보를 반환한다.")
    @Test
    void givenReviewAndExistentPlaceInfo_whenCreateReview_thenReturnSavedReviewInfo() {
        // given
        long writerId = 1L;
        long placeId = 2L;
        ReviewCreateRequest reviewCreateRequest = createReviewCreateRequest(placeId);
        String kakaoPid = "12345";
        Place expectedPlace = createPlace(placeId, kakaoPid);
        Member expectedMember = createMember(writerId);
        Review createdReview = createReview(3L, expectedMember, expectedPlace);
        ReviewKeyword reviewKeyword = createReviewKeyword(4L, createdReview, ReviewKeywordValue.FRESH);
        createdReview.getKeywords().add(reviewKeyword);
        ReviewImage reviewImage = createReviewImage(100L, createdReview);
        createdReview.getReviewImages().add(reviewImage);
        given(placeQueryService.findById(placeId)).willReturn(expectedPlace);
        given(memberQueryService.findById(writerId)).willReturn(expectedMember);
        given(bookmarkQueryService.isMarkedPlace(writerId, expectedPlace)).willReturn(false);
        given(reviewRepository.save(any(Review.class))).willReturn(createdReview);
        given(reviewKeywordRepository.save(any(ReviewKeyword.class))).willReturn(reviewKeyword);
        given(reviewImageService.upload(any(Review.class), any())).willReturn(List.of(reviewImage));
        given(reviewImageMenuTagRepository.saveAll(anyList())).willReturn(List.of());
        willDoNothing().given(placeCommandService).renewTop3Keywords(expectedPlace);

        // when
        ReviewDto actualSavedReview = sut.create(writerId, reviewCreateRequest);

        // then
        then(placeQueryService).should().findById(placeId);
        then(memberQueryService).should().findById(writerId);
        then(bookmarkQueryService).should().isMarkedPlace(writerId, expectedPlace);
        then(reviewRepository).should().save(any(Review.class));
        then(reviewKeywordRepository).should().save(any(ReviewKeyword.class));
        verify(reviewImageService, times(reviewCreateRequest.getImages().size())).upload(any(Review.class), any());
        then(reviewImageMenuTagRepository).should().saveAll(anyList());
        then(placeCommandService).should().renewTop3Keywords(expectedPlace);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualSavedReview.getPlace().getKakaoPid()).isEqualTo(kakaoPid);
    }

    @DisplayName("리뷰를 삭제하면, 리뷰와 모든 리뷰 이미지들을 soft delete하고 모든 리뷰 키워드들을 삭제한다.")
    @Test
    void given_whenSoftDeleteReview_thenSoftDeleteReviewAndAllReviewImagesAndDeleteAllReviewKeywords() {
        // given
        long memberId = 1L;
        long reviewId = 3L;
        Member loginMember = createMember(memberId);
        Place place = createPlace(2L, "2");
        Review findReview = createReview(reviewId, loginMember, place);
        ReviewKeyword reviewKeyword = createReviewKeyword(5L, findReview, ReviewKeywordValue.FRESH);
        findReview.getKeywords().add(reviewKeyword);
        ReviewImage reviewImage = createReviewImage(6L, findReview);
        findReview.getReviewImages().add(reviewImage);
        given(memberQueryService.findById(memberId)).willReturn(loginMember);
        given(reviewQueryService.findById(reviewId)).willReturn(findReview);
        willDoNothing().given(reviewImageService).softDeleteAll(findReview.getReviewImages());
        willDoNothing().given(reviewKeywordRepository).deleteAll(findReview.getKeywords());
        willDoNothing().given(reviewRepository).flush();
        willDoNothing().given(placeCommandService).renewTop3Keywords(findReview.getPlace());

        // when
        sut.delete(memberId, reviewId);

        // then
        then(memberQueryService).should().findById(memberId);
        then(reviewQueryService).should().findById(reviewId);
        then(reviewImageService).should().softDeleteAll(findReview.getReviewImages());
        then(reviewKeywordRepository).should().deleteAll(findReview.getKeywords());
        then(reviewRepository).should().flush();
        then(placeCommandService).should().renewTop3Keywords(findReview.getPlace());
        verifyEveryMocksShouldHaveNoMoreInteractions();
    }

    @DisplayName("리뷰 삭제 권한이 없는 회원의 PK가 주어지고, 리뷰를 soft delete하면, 예외가 발생한다.")
    @Test
    void givenUnauthorizedMemberId_whenSoftDeleteReview_thenThrowException() {
        // given
        long loginMemberId = 1L;
        long reviewWriterId = 2L;
        long reviewId = 3L;
        Place place = createPlace(4L, "2");
        Member loginMember = createMember(loginMemberId);
        Member reviewWriter = createMember(reviewWriterId);
        Review findReview = createReview(reviewId, reviewWriter, place);
        ReviewKeyword reviewKeyword = createReviewKeyword(5L, findReview, ReviewKeywordValue.FRESH);
        findReview.getKeywords().add(reviewKeyword);
        ReviewImage reviewImage = createReviewImage(6L, findReview);
        findReview.getReviewImages().add(reviewImage);
        given(memberQueryService.findById(loginMemberId)).willReturn(loginMember);
        given(reviewQueryService.findById(reviewId)).willReturn(findReview);

        // when
        Throwable t = catchThrowable(() -> sut.delete(loginMemberId, reviewId));

        // then
        then(memberQueryService).should().findById(loginMemberId);
        then(reviewQueryService).should().findById(reviewId);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(t).isInstanceOf(ReviewDeletePermissionDeniedException.class);
    }

    private void verifyEveryMocksShouldHaveNoMoreInteractions() {
        then(reviewQueryService).shouldHaveNoMoreInteractions();
        then(reviewImageService).shouldHaveNoMoreInteractions();
        then(memberQueryService).shouldHaveNoMoreInteractions();
        then(placeCommandService).shouldHaveNoMoreInteractions();
        then(placeQueryService).shouldHaveNoMoreInteractions();
        then(reviewRepository).shouldHaveNoMoreInteractions();
        then(reviewKeywordRepository).shouldHaveNoMoreInteractions();
        then(bookmarkQueryService).shouldHaveNoMoreInteractions();
        then(reviewImageMenuTagRepository).shouldHaveNoMoreInteractions();
    }

    private Member createMember(Long memberId) {
        return createMember(memberId, Set.of(RoleType.USER));
    }

    private Member createMember(Long memberId, Set<RoleType> roleTypes) {
        return Member.of(
                memberId,
                "profile image url",
                "profile thunmbnail image url",
                "social user id",
                LoginType.KAKAO,
                roleTypes,
                "email",
                "nickname",
                LocalDate.of(2000, 1, 1),
                20,
                Gender.MALE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private Place createPlace(long id, String kakaoPid) {
        return Place.of(
                id,
                List.of(ReviewKeywordValue.FRESH),
                kakaoPid,
                "place name",
                "page url",
                KakaoCategoryGroupCode.FD6,
                new PlaceCategory("한식", "냉면", null),
                null,
                new Address("sido", "sgg", "lot number address", "road address"),
                null,
                new Point("37.5595073462493", "126.921462488105"),
                "",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private Review createReview(Long reviewId, Member member, Place place) {
        return Review.of(
                reviewId,
                member,
                place,
                "자동 생성된 내용",
                "제출한 내용",
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private ReviewKeyword createReviewKeyword(Long reviewKeywordId, Review review, ReviewKeywordValue reviewKeywordValue) {
        return ReviewKeyword.of(
                reviewKeywordId,
                review,
                reviewKeywordValue,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static ReviewImage createReviewImage(Long reviewImageId, Review review) {
        return ReviewImage.of(
                reviewImageId,
                review,
                "original file name",
                "stored file name",
                "url",
                "thumbnail stored file name",
                "thumbnail url",
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private ReviewCreateRequest createReviewCreateRequest(long placeId) {
        return ReviewCreateRequest.of(
                placeId,
                List.of(ReviewKeywordValue.FRESH),
                "자동 생성된 내용",
                "제출한 내용",
                List.of(createReviewImageCreateRequest())
        );
    }

    private ReviewImageCreateRequest createReviewImageCreateRequest() {
        return new ReviewImageCreateRequest(
                createMockMultipartFile(),
                List.of(
                        createReviewMenuTagCreateRequest("치킨"),
                        createReviewMenuTagCreateRequest("피자")
                )
        );
    }

    private ReviewMenuTagCreateRequest createReviewMenuTagCreateRequest(String content) {
        return new ReviewMenuTagCreateRequest(content, new MenuTagPointCreateRequest("10.0", "50.0"));
    }

    public static MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile(
                "test",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test".getBytes()
        );
    }
}