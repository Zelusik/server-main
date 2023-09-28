package com.zelusik.eatery.domain.recommended_review.service;

import com.zelusik.eatery.domain.member.service.MemberQueryService;
import com.zelusik.eatery.domain.recommended_review.entity.RecommendedReview;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.review.entity.Review;
import com.zelusik.eatery.domain.recommended_review.dto.RecommendedReviewDto;
import com.zelusik.eatery.domain.recommended_review.dto.request.BatchUpdateRecommendedReviewsRequest;
import com.zelusik.eatery.domain.recommended_review.repository.RecommendedReviewRepository;
import com.zelusik.eatery.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecommendedReviewService {

    private final MemberQueryService memberQueryService;
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
        Member member = memberQueryService.findById(memberId);
        Review review = reviewService.findById(reviewId);

        RecommendedReview recommendedReview = RecommendedReview.of(member, review, ranking);
        recommendedReviewRepository.save(recommendedReview);

        return RecommendedReviewDto.fromWithoutReviewWriterAndPlace(recommendedReview);
    }

    /**
     * 특정 회원이 설정한 추천 리뷰들을 장소 저장 여부와 함께 조회한다.
     *
     * @param memberId 추천 리뷰를 조회하고자 하는 대상 회원의 PK
     * @return 조회된 추천 리뷰들의 dto
     */
    public List<RecommendedReviewDto> findAllDtosWithPlaceMarkedStatus(long memberId) {
        return recommendedReviewRepository.findAllDtosWithPlaceMarkedStatusByMemberId(memberId);
    }

    /**
     * <p>추천 리뷰를 갱신한다.
     * <p>기존 등록된 추천 리뷰 내역을 전부 삭제한 후, 새로 전달받은 추천 리뷰 목록으로 대체한다.
     *
     * @param memberId                             PK of login member
     * @param batchUpdateRecommendedReviewsRequest 갱신할 추천 리뷰 목록이 담긴 request dto
     * @return 갱신된 추천 리뷰 목록
     */
    @Transactional
    public List<RecommendedReviewDto> batchUpdateRecommendedReviews(long memberId, BatchUpdateRecommendedReviewsRequest batchUpdateRecommendedReviewsRequest) {
        Member member = memberQueryService.findById(memberId);
        recommendedReviewRepository.deleteAllByMember(member);
        recommendedReviewRepository.flush();

        List<RecommendedReview> recommendedReviewsForBatchUpdate = batchUpdateRecommendedReviewsRequest.getRecommendedReviews().stream()
                .map(request -> {
                    Review review = reviewService.findById(request.getReviewId());
                    return RecommendedReview.of(member, review, request.getRanking());
                })
                .toList();

        recommendedReviewRepository.saveAll(recommendedReviewsForBatchUpdate);

        return recommendedReviewsForBatchUpdate.stream()
                .map(RecommendedReviewDto::fromWithoutReviewWriterAndPlace)
                .toList();
    }
}
