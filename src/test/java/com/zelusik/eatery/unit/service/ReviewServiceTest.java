package com.zelusik.eatery.unit.service;

import com.zelusik.eatery.global.common.constant.EateryConstants;
import com.zelusik.eatery.global.common.constant.FoodCategoryValue;
import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.bookmark.service.BookmarkService;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.member.service.MemberService;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.place.service.PlaceService;
import com.zelusik.eatery.domain.review.entity.Review;
import com.zelusik.eatery.domain.review.service.ReviewService;
import com.zelusik.eatery.domain.review_image.entity.ReviewImage;
import com.zelusik.eatery.domain.review_image.service.ReviewImageService;
import com.zelusik.eatery.domain.review_keyword.entity.ReviewKeyword;
import com.zelusik.eatery.domain.member.dto.MemberDto;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.review.dto.ReviewDto;
import com.zelusik.eatery.domain.review_image.dto.ReviewImageDto;
import com.zelusik.eatery.domain.review_image_menu_tag.dto.request.MenuTagPointCreateRequest;
import com.zelusik.eatery.domain.review.dto.request.ReviewCreateRequest;
import com.zelusik.eatery.domain.review_image.dto.request.ReviewImageCreateRequest;
import com.zelusik.eatery.domain.review_image_menu_tag.dto.request.ReviewMenuTagCreateRequest;
import com.zelusik.eatery.domain.review.exception.ReviewDeletePermissionDeniedException;
import com.zelusik.eatery.domain.review.exception.ReviewNotFoundException;
import com.zelusik.eatery.domain.review_image_menu_tag.repository.ReviewImageMenuTagRepository;
import com.zelusik.eatery.domain.review_keyword.repository.ReviewKeywordRepository;
import com.zelusik.eatery.domain.review.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.zelusik.eatery.domain.review.constant.ReviewEmbedOption.PLACE;
import static com.zelusik.eatery.domain.review.constant.ReviewEmbedOption.WRITER;
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
        given(placeService.findById(placeId)).willReturn(expectedPlace);
        given(memberService.findById(writerId)).willReturn(expectedMember);
        given(bookmarkService.isMarkedPlace(writerId, expectedPlace)).willReturn(false);
        given(reviewRepository.save(any(Review.class))).willReturn(createdReview);
        given(reviewKeywordRepository.save(any(ReviewKeyword.class))).willReturn(reviewKeyword);
        given(reviewImageService.upload(any(Review.class), any())).willReturn(List.of(reviewImage));
        given(reviewImageMenuTagRepository.saveAll(anyList())).willReturn(List.of());
        willDoNothing().given(placeService).renewTop3Keywords(expectedPlace);

        // when
        ReviewDto actualSavedReview = sut.create(writerId, reviewCreateRequest);

        // then
        then(placeService).should().findById(placeId);
        then(memberService).should().findById(writerId);
        then(bookmarkService).should().isMarkedPlace(writerId, expectedPlace);
        then(reviewRepository).should().save(any(Review.class));
        then(reviewKeywordRepository).should().save(any(ReviewKeyword.class));
        verify(reviewImageService, times(reviewCreateRequest.getImages().size())).upload(any(Review.class), any());
        then(reviewImageMenuTagRepository).should().saveAll(anyList());
        then(placeService).should().renewTop3Keywords(expectedPlace);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualSavedReview.getPlace().getKakaoPid()).isEqualTo(kakaoPid);
    }

    @DisplayName("리뷰의 id(PK)가 주어지고, id로 리뷰를 단건 조회하면, 조회된 리뷰가 반환된다.")
    @Test
    void givenReviewId_whenFindReviewById_thenReturnReview() {
        // given
        long memberId = 1L;
        long reviewId = 2L;
        Review expectedResult = createReview(reviewId, createMember(memberId), createPlace(3L, "12345"));
        given(reviewRepository.findByIdAndDeletedAtNull(reviewId)).willReturn(Optional.of(expectedResult));

        // when
        Review actualResult = sut.findById(reviewId);

        // then
        then(reviewRepository).should().findByIdAndDeletedAtNull(reviewId);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult)
                .hasFieldOrPropertyWithValue("id", reviewId)
                .hasFieldOrPropertyWithValue("writer.id", memberId);
    }

    @DisplayName("존재하지 않는 리뷰 id(PK)가 주어지고, 주어진 id로 리뷰를 단건 조회하면, 예외가 발생한다..")
    @Test
    void givenNotExistentReviewId_whenFindReviewById_thenThrowReviewNotFoundException() {
        // given
        long reviewId = 2L;
        given(reviewRepository.findByIdAndDeletedAtNull(reviewId)).willReturn(Optional.empty());

        // when
        Throwable t = catchThrowable(() -> sut.findById(reviewId));

        // then
        then(reviewRepository).should().findByIdAndDeletedAtNull(reviewId);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(t).isInstanceOf(ReviewNotFoundException.class);
    }

    @DisplayName("리뷰의 id(PK)가 주어지고, id로 리뷰 dto를 단건 조회하면, 조회된 리뷰의 dto가 반환된다.")
    @Test
    void givenReviewId_whenFindReviewDtoById_thenReturnReviewDto() {
        // given
        long memberId = 1L;
        long reviewId = 2L;
        Review expectedResult = createReview(reviewId, createMember(memberId), createPlace(3L, "12345"));
        given(reviewRepository.findByIdAndDeletedAtNull(reviewId)).willReturn(Optional.of(expectedResult));
        given(bookmarkService.isMarkedPlace(eq(memberId), any(Place.class))).willReturn(false);

        // when
        ReviewDto actualResult = sut.findDtoById(memberId, reviewId);

        // then
        then(reviewRepository).should().findByIdAndDeletedAtNull(reviewId);
        then(bookmarkService).should().isMarkedPlace(eq(memberId), any(Place.class));
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResult)
                .hasFieldOrPropertyWithValue("id", reviewId)
                .hasFieldOrPropertyWithValue("writer.id", memberId);
    }

    @DisplayName("리뷰 목록을 조회하면, 조회된 리뷰 목록(Slice)을 반환한다.")
    @Test
    void given_whenFindDtos_thenReturnReviews() {
        // given
        long loginMemberId = 1L;
        Pageable pageable = Pageable.ofSize(15);
        MemberDto member = createMemberDto(3L);
        PlaceDto place = createPlaceDto(4L, "12345");
        Slice<ReviewDto> expectedSearchResult = new SliceImpl<>(List.of(createReviewDto(2L, member, place)));
        given(reviewRepository.findDtos(loginMemberId, null, null, List.of(WRITER, PLACE), pageable)).willReturn(expectedSearchResult);

        // when
        Slice<ReviewDto> actualSearchResult = sut.findDtos(loginMemberId, null, null, List.of(WRITER, PLACE), pageable);

        // then
        then(reviewRepository).should().findDtos(loginMemberId, null, null, List.of(WRITER, PLACE), pageable);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualSearchResult).hasSize(expectedSearchResult.getSize());
    }

    @DisplayName("리뷰 피드를 조회한다.")
    @Test
    void given_whenFindReviewFeed_thenReturnResults() {
        // given
        long loginMemberId = 1L;
        long reviewId = 2L;
        Pageable pageable = Pageable.ofSize(10);
        MemberDto writer = createMemberDto(3L);
        PlaceDto place = createPlaceDto(4L, "123");
        Slice<ReviewDto> expectedResults = new SliceImpl<>(List.of(createReviewDto(reviewId, writer, place)), pageable, false);
        given(reviewRepository.findReviewFeed(loginMemberId, pageable)).willReturn(expectedResults);

        // when
        Slice<ReviewDto> actualResults = sut.findReviewReed(loginMemberId, Pageable.ofSize(10));

        // then
        then(reviewRepository).should().findReviewFeed(loginMemberId, pageable);
        verifyEveryMocksShouldHaveNoMoreInteractions();
        assertThat(actualResults).hasSize(expectedResults.getNumberOfElements());
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
        Place place = createPlace(4L, "2");
        Member loginMember = createMember(loginMemberId);
        Member reviewWriter = createMember(reviewWriterId);
        Review findReview = createReview(reviewId, reviewWriter, place);
        ReviewKeyword reviewKeyword = createReviewKeyword(5L, findReview, ReviewKeywordValue.FRESH);
        findReview.getKeywords().add(reviewKeyword);
        ReviewImage reviewImage = createReviewImage(6L, findReview);
        findReview.getReviewImages().add(reviewImage);
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

    private MemberDto createMemberDto(Long memberId) {
        return createMemberDto(memberId, Set.of(RoleType.USER));
    }

    private MemberDto createMemberDto(Long memberId, Set<RoleType> roleTypes) {
        return new MemberDto(
                memberId,
                EateryConstants.defaultProfileImageUrl,
                EateryConstants.defaultProfileThumbnailImageUrl,
                "1234567890",
                LoginType.KAKAO,
                roleTypes,
                "test@test.com",
                "test",
                LocalDate.of(1998, 1, 5),
                20,
                Gender.MALE,
                List.of(FoodCategoryValue.KOREAN),
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

    public static PlaceDto createPlaceDto(long placeId, String kakaoPid) {
        return new PlaceDto(
                placeId,
                List.of(ReviewKeywordValue.FRESH),
                kakaoPid,
                "연남토마 본점",
                "http://place.map.kakao.com/308342289",
                KakaoCategoryGroupCode.FD6,
                PlaceCategory.of("음식점 > 퓨전요리 > 퓨전일식"),
                "02-332-8064",
                Address.of("서울 마포구 연남동 568-26", "서울 마포구 월드컵북로6길 61"),
                "http://place.map.kakao.com/308342289",
                new Point("37.5595073462493", "126.921462488105"),
                null,
                List.of(),
                null,
                false
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

    public static ReviewDto createReviewDto(long reviewId, MemberDto writer, PlaceDto place) {
        return new ReviewDto(
                reviewId,
                writer,
                place,
                List.of(ReviewKeywordValue.NOISY, ReviewKeywordValue.FRESH),
                "자동 생성된 내용",
                "제출된 내용",
                List.of(new ReviewImageDto(
                        1L,
                        1L,
                        "test.txt",
                        "storedName",
                        "url",
                        "thumbnailStoredName",
                        "thumbnailUrl")),
                LocalDateTime.now()
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