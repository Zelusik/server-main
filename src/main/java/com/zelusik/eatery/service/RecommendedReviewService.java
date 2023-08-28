package com.zelusik.eatery.service;

import com.zelusik.eatery.domain.RecommendedReview;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.dto.recommended_review.RecommendedReviewDto;
import com.zelusik.eatery.repository.recommended_review.RecommendedReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecommendedReviewService {

    private final MemberService memberService;
    private final ReviewService reviewService;
    private final RecommendedReviewRepository recommendedReviewRepository;

    /**
     * 추천 리뷰를 등록한다.
     *
     * @param memberId PK of login member
     * @param reviewId 추천 리뷰로 등록하고자 하는 리뷰의 PK
     * @param ranking  추천 순위. 1~3의 값만 가능.
     * @return 저장된 추천 리뷰 dto
     */
    @Transactional
    public RecommendedReviewDto saveRecommendedReview(long memberId, long reviewId, short ranking) {
        Member member = memberService.findById(memberId);
        Review review = reviewService.findById(reviewId);

        RecommendedReview recommendedReview = RecommendedReview.of(member, review, ranking);
        recommendedReviewRepository.save(recommendedReview);

        return RecommendedReviewDto.from(recommendedReview);
    }
}
