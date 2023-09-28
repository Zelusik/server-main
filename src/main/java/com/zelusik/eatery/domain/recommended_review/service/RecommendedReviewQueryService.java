package com.zelusik.eatery.domain.recommended_review.service;

import com.zelusik.eatery.domain.recommended_review.dto.RecommendedReviewDto;
import com.zelusik.eatery.domain.recommended_review.repository.RecommendedReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecommendedReviewQueryService {

    private final RecommendedReviewRepository recommendedReviewRepository;

    /**
     * 특정 회원이 설정한 추천 리뷰들을 장소 저장 여부와 함께 조회한다.
     *
     * @param memberId 추천 리뷰를 조회하고자 하는 대상 회원의 PK
     * @return 조회된 추천 리뷰들의 dto
     */
    public List<RecommendedReviewDto> findAllDtosWithPlaceMarkedStatus(long memberId) {
        return recommendedReviewRepository.findAllDtosWithPlaceMarkedStatusByMemberId(memberId);
    }
}
