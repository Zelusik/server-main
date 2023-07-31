package com.zelusik.eatery.util;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.review.MenuTagPoint;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.domain.review.ReviewImage;
import com.zelusik.eatery.domain.review.ReviewKeyword;
import com.zelusik.eatery.dto.review.ReviewDto;
import com.zelusik.eatery.dto.review.ReviewImageDto;
import com.zelusik.eatery.dto.review.request.ReviewCreateRequest;
import com.zelusik.eatery.dto.review.request.ReviewImageCreateRequest;
import com.zelusik.eatery.dto.review.request.ReviewMenuTagCreateRequest;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewTestUtils {

    public static ReviewCreateRequest createReviewCreateRequest() {
        return ReviewCreateRequest.of(
                PlaceTestUtils.createPlaceRequest(),
                List.of("신선한 재료"),
                "자동 생성된 내용",
                "제출한 내용",
                List.of(createReviewImageCreateRequest())
        );
    }

    @NotNull
    public static ReviewImageCreateRequest createReviewImageCreateRequest() {
        return new ReviewImageCreateRequest(
                MultipartFileTestUtils.createMockMultipartFile(),
                List.of(
                        createReviewMenuTagCreateRequest("치킨"),
                        createReviewMenuTagCreateRequest("피자")
                )
        );
    }

    public static ReviewDto createReviewDto() {
        return ReviewDto.of(
                1L,
                MemberTestUtils.createMemberDtoWithId(),
                PlaceTestUtils.createPlaceDto(),
                List.of(ReviewKeywordValue.NOISY, ReviewKeywordValue.FRESH),
                "자동 생성된 내용",
                "제출된 내용",
                List.of(ReviewImageDto.of(
                        1L,
                        1L,
                        "test.txt",
                        "storedName",
                        "url",
                        "thumbnailStoredName",
                        "thumbnailUrl",
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static ReviewDto createReviewDtoWithoutPlace() {
        return ReviewDto.of(
                1L,
                MemberTestUtils.createMemberDtoWithId(),
                null,
                List.of(ReviewKeywordValue.NOISY, ReviewKeywordValue.FRESH),
                "자동 생성된 내용",
                "제출된 내용",
                List.of(ReviewImageDto.of(
                        1L,
                        1L,
                        "test.txt",
                        "storedName",
                        "url",
                        "thumbnailStoredName",
                        "thumbnailUrl",
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static Review createNotSavedReview(Member writer, Place place) {
        return Review.of(
                writer,
                place,
                "자동 생성된 내용",
                "제출한 내용"
        );
    }

    public static Review createReviewWithKeywordsAndImages(Long reviewId, Member member, Place place) {
        Review review = createReview(reviewId, member, place);
        review.getKeywords().add(createReviewKeyword(10L, review, ReviewKeywordValue.BEST_FLAVOR));
        review.getReviewImages().add(createReviewImage(11L, review));
        return review;
    }

    public static Review createReview(Long reviewId, Long memberId, Long placeId, String kakaoPid, Long reviewKeywordId, Long reviewImageId) {
        Review review = createReview(
                reviewId,
                MemberTestUtils.createMember(memberId),
                PlaceTestUtils.createPlace(placeId, kakaoPid)
        );
        review.getKeywords().add(createReviewKeyword(reviewKeywordId, review, ReviewKeywordValue.BEST_FLAVOR));
        review.getReviewImages().add(createReviewImage(reviewImageId, review));
        return review;
    }

    public static Review createReview(Long reviewId, Member member, Place place, List<ReviewKeyword> reviewKeywords, List<ReviewImage> reviewImages) {
        Review review = createReview(reviewId, member, place);
        reviewKeywords.forEach(reviewKeyword -> review.getKeywords().add(reviewKeyword));
        reviewImages.forEach(reviewImage -> review.getReviewImages().add(reviewImage));
        return review;
    }

    public static Review createReview(Long reviewId, Member member, Place place) {
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

    public static ReviewKeyword createReviewKeyword(
            Long reviewKeywordId,
            Review review,
            ReviewKeywordValue reviewKeywordValue
    ) {
        return ReviewKeyword.of(
                reviewKeywordId,
                review,
                reviewKeywordValue,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static ReviewImage createNotSavedReviewImage(Review review) {
        return ReviewImage.of(
                review,
                "original file name",
                "stored file name",
                "url",
                "thumbnail stored file name",
                "thumbnail url"
        );
    }

    public static ReviewImage createReviewImage(
            Long reviewImageId,
            Review review
    ) {
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

    public static ReviewMenuTagCreateRequest createReviewMenuTagCreateRequest(String content) {
        return new ReviewMenuTagCreateRequest(content, new MenuTagPoint(10.0, 50.0));
    }
}
