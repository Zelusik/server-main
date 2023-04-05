package com.zelusik.eatery.app.repository.review;

import com.zelusik.eatery.app.config.QuerydslConfig;
import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.domain.review.Review;
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

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Repository] Review")
@Import(QuerydslConfig.class)
@ActiveProfiles("test")
@DataJpaTest
class ReviewRepositoryTest {

    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;

    public ReviewRepositoryTest(
            @Autowired MemberRepository memberRepository,
            @Autowired PlaceRepository placeRepository,
            @Autowired ReviewRepository reviewRepository
    ) {
        this.memberRepository = memberRepository;
        this.placeRepository = placeRepository;
        this.reviewRepository = reviewRepository;
    }

    @DisplayName("리뷰가 존재하고, 리뷰의 soft delete를 진행하면, 리뷰의 deletedAt 정보가 갱신된다.")
    @Test
    void givenReview_whenSoftDeleteReview_thenUpdateDeletedAt() {
        // given
        Member member = memberRepository.save(MemberTestUtils.createNotSavedMember());
        Place place = placeRepository.save(PlaceTestUtils.createNotSavedPlace("1234"));
        Review review = reviewRepository.save(ReviewTestUtils.createNotSavedReview(member, place));

        // when
        reviewRepository.softDelete(review);

        // then
        Review updatedReview = reviewRepository.findById(review.getId()).orElseThrow(EntityNotFoundException::new);
        assertThat(review.getDeletedAt()).isNull();
        assertThat(updatedReview.getDeletedAt()).isNotNull();
    }
}