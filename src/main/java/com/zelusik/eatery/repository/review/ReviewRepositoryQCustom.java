package com.zelusik.eatery.repository.review;

import com.zelusik.eatery.constant.review.ReviewEmbedOption;
import com.zelusik.eatery.dto.review.ReviewDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ReviewRepositoryQCustom {

    /**
     * 리뷰 조회
     *
     * @param loginMemberId 로그인 회원
     * @param writerId      filter - 특정 회원이 작성한 리뷰만 조회
     * @param placeId       filter - 특정 가게에 대한 리뷰만 조회
     * @param embed         연관된 entity를 포함할지에 대한 여부
     * @param pageable      paging 정보
     * @return 조회된 리뷰 목록(Slice)
     */
    Slice<ReviewDto> findDtos(Long loginMemberId, Long writerId, Long placeId, List<ReviewEmbedOption> embed, Pageable pageable);
}
