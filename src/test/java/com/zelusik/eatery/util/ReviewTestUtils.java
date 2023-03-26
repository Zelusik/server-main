package com.zelusik.eatery.util;

import com.zelusik.eatery.app.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.domain.review.Review;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.domain.review.ReviewKeyword;
import com.zelusik.eatery.app.dto.review.ReviewDtoWithMember;
import com.zelusik.eatery.app.dto.review.ReviewDtoWithMemberAndPlace;
import com.zelusik.eatery.app.dto.review.ReviewFileDto;
import com.zelusik.eatery.app.dto.review.request.ReviewCreateRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewTestUtils {

    public static ReviewCreateRequest createReviewCreateRequest() {
        return ReviewCreateRequest.of(
                PlaceTestUtils.createPlaceRequest(),
                List.of("신선한 재료"),
                "자동 생성된 내용",
                "제출한 내용",
                List.of(MultipartFileTestUtils.createMockMultipartFile())
        );
    }

    public static ReviewDtoWithMemberAndPlace createReviewDtoWithMemberAndPlace() {
        return ReviewDtoWithMemberAndPlace.of(
                1L,
                MemberTestUtils.createMemberDtoWithId(),
                PlaceTestUtils.createPlaceDtoWithOpeningHours(),
                List.of(ReviewKeywordValue.NOISY, ReviewKeywordValue.FRESH),
                "자동 생성된 내용",
                "제출된 내용",
                List.of(ReviewFileDto.of(
                        1L,
                        1L,
                        "test.txt",
                        "storedName",
                        "url",
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static ReviewDtoWithMember createReviewDtoWithMember() {
        return ReviewDtoWithMember.of(
                1L,
                MemberTestUtils.createMemberDtoWithId(),
                List.of(ReviewKeywordValue.NOISY, ReviewKeywordValue.FRESH),
                "자동 생성된 내용",
                "제출된 내용",
                List.of(ReviewFileDto.of(
                        1L,
                        1L,
                        "test.txt",
                        "storedName",
                        "url",
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static Review createReviewWithId() {
        return createReviewWithId(
                MemberTestUtils.createMember(1L),
                PlaceTestUtils.createPlace()
        );
    }

    public static Review createReviewWithId(Member member, Place place) {
        Review review = Review.of(
                member,
                place,
                "자동 생성된 내용",
                "제출한 내용"
        );
        ReflectionTestUtils.setField(review, "id", 1L);
        return review;
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
}
