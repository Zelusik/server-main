package com.zelusik.eatery.app.repository.review;

import com.zelusik.eatery.app.constant.review.ReviewKeywordValue;

import java.util.List;

public interface ReviewKeywordJdbcTemplateRepository {

    /**
     * 특정 장소에 대한 top 3 keywords를 DB에서 계산하여 조회한다.
     *
     * @param placeId top 3 keywords를 조회하고자 하는 장소의 PK
     * @return 조회된 top 3 keywords
     */
    List<ReviewKeywordValue> searchTop3Keywords(Long placeId);
}
