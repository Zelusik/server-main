package com.zelusik.eatery.domain.review_keyword.repository;

import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;

import java.util.List;

public interface ReviewKeywordRepositoryJCustom {

    /**
     * 특정 장소에 대한 top 3 keywords를 DB에서 계산하여 조회한다.
     *
     * @param placeId top 3 keywords를 조회하고자 하는 장소의 PK
     * @return 조회된 top 3 keywords
     */
    List<ReviewKeywordValue> searchTop3Keywords(Long placeId);
}
