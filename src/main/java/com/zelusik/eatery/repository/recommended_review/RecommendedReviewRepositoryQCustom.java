package com.zelusik.eatery.repository.recommended_review;

import com.zelusik.eatery.dto.recommended_review.RecommendedReviewDto;

import java.util.List;

public interface RecommendedReviewRepositoryQCustom {

    /**
     * 특정 회원이 설정한 추천 리뷰들을 장소 저장 여부와 함께 조회한다.
     *
     * @param memberId 추천 리뷰를 조회하고자 하는 대상 회원의 PK
     * @return 조회된 추천 리뷰들의 dto
     */
    List<RecommendedReviewDto> findAllDtosWithPlaceMarkedStatusByMemberId(long memberId);
}
