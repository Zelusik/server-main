package com.zelusik.eatery.app.repository.review;

import com.zelusik.eatery.app.config.QuerydslConfig;
import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.domain.review.Review;
import com.zelusik.eatery.app.domain.review.ReviewImage;
import com.zelusik.eatery.app.repository.member.MemberRepository;
import com.zelusik.eatery.app.repository.place.PlaceRepository;
import com.zelusik.eatery.util.MemberTestUtils;
import com.zelusik.eatery.util.PlaceTestUtils;
import com.zelusik.eatery.util.ReviewTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Repository] ReviewImage")
@Import(QuerydslConfig.class)
@ActiveProfiles("test")
@DataJpaTest
class ReviewImageRepositoryTest {

    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;

    public ReviewImageRepositoryTest(
            @Autowired MemberRepository memberRepository,
            @Autowired PlaceRepository placeRepository,
            @Autowired ReviewRepository reviewRepository,
            @Autowired ReviewImageRepository reviewImageRepository
    ) {
        this.memberRepository = memberRepository;
        this.placeRepository = placeRepository;
        this.reviewRepository = reviewRepository;
        this.reviewImageRepository = reviewImageRepository;
    }

    @DisplayName("리뷰 이미지가 주어지고, soft delete를 수행하면, 리뷰 이미지의 deletedAt을 업데이트한다.")
    @Test
    void givenReviewImage_whenSoftDelete_thenUpdateDeletedAt() {
        // given
        Member member = memberRepository.save(MemberTestUtils.createNotSavedMember());
        Place place = placeRepository.save(PlaceTestUtils.createNotSavedPlace("1234"));
        Review review = reviewRepository.save(ReviewTestUtils.createNotSavedReview(member, place));
        ReviewImage reviewImage = reviewImageRepository.save(ReviewTestUtils.createNotSavedReviewImage(review));

        // when
        reviewImageRepository.softDelete(reviewImage);

        // then
        ReviewImage deletedReviewImage = reviewImageRepository.findById(reviewImage.getId()).orElseThrow(EntityNotFoundException::new);
        assertThat(reviewImage.getDeletedAt()).isNull();
        assertThat(deletedReviewImage.getDeletedAt()).isNotNull();
    }

    @DisplayName("리뷰 이미지들이 주어지고, 모든 리뷰 이미지에 대해 soft delete를 수행하면, 모든 리뷰 이미지의 deletedAt을 업데이트한다.")
    @Test
    void givenReviewImages_whenSoftDeleteAll_thenUpdateEveryDeletedAt() {
        // given
        Member member = memberRepository.save(MemberTestUtils.createNotSavedMember());
        Place place = placeRepository.save(PlaceTestUtils.createNotSavedPlace("1234"));
        Review review = reviewRepository.save(ReviewTestUtils.createNotSavedReview(member, place));
        List<ReviewImage> reviewImages = List.of(
                reviewImageRepository.save(ReviewTestUtils.createNotSavedReviewImage(review)),
                reviewImageRepository.save(ReviewTestUtils.createNotSavedReviewImage(review)),
                reviewImageRepository.save(ReviewTestUtils.createNotSavedReviewImage(review)),
                reviewImageRepository.save(ReviewTestUtils.createNotSavedReviewImage(review)),
                reviewImageRepository.save(ReviewTestUtils.createNotSavedReviewImage(review))
        );

        // when
        reviewImageRepository.softDeleteAll(reviewImages);

        // then
        List<ReviewImage> allReviewImages = reviewImageRepository.findAll();
        assertThat(allReviewImages.size()).isEqualTo(reviewImages.size());
        reviewImages.forEach(reviewImage ->
                assertThat(reviewImage.getDeletedAt()).isNull());
        allReviewImages.forEach(deletedReviewImage ->
                assertThat(deletedReviewImage.getDeletedAt()).isNotNull());
    }
}