package com.zelusik.eatery.util;

import com.zelusik.eatery.app.domain.Member;
import com.zelusik.eatery.app.domain.Review;
import com.zelusik.eatery.app.domain.constant.ReviewKeyword;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.dto.review.ReviewDto;
import com.zelusik.eatery.app.dto.review.ReviewFileDto;
import com.zelusik.eatery.app.dto.review.request.ReviewCreateRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewTestUtils {

    public static ReviewCreateRequest createReviewCreateRequest() {
        return ReviewCreateRequest.of(
                PlaceTestUtils.createPlaceRequest(),
                List.of(ReviewKeyword.NOISY, ReviewKeyword.FRESH),
                "자동 생성된 내용",
                "제출한 내용",
                List.of(MultipartFileTestUtils.createMockMultipartFile())
        );
    }

    public static ReviewDto createReviewDtoWithId() {
        return ReviewDto.of(
                1L,
                MemberTestUtils.createMemberDtoWithId(),
                PlaceTestUtils.createPlaceDtoWithIdAndOpeningHours(),
                List.of(ReviewKeyword.NOISY, ReviewKeyword.FRESH),
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

    public static Review createReviewWithId(Member member, Place place) {
        Review review = Review.of(
                member,
                place,
                List.of(ReviewKeyword.NOISY, ReviewKeyword.FRESH),
                "자동 생성된 내용",
                "제출한 내용"
        );
        ReflectionTestUtils.setField(review, "id", 1L);
        return review;
    }
}
