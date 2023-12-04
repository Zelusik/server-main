package com.zelusik.eatery.domain.review.repository;

import com.zelusik.eatery.domain.review.constant.ReviewEmbedOption;
import com.zelusik.eatery.domain.review.dto.ReviewWithPlaceMarkedStatusDto;
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
    Slice<ReviewWithPlaceMarkedStatusDto> findDtos(Long loginMemberId, Long writerId, Long placeId, List<ReviewEmbedOption> embed, Pageable pageable);

    /**
     * <p>리뷰 피드를 조회한다.
     * <p>내가 작성한 리뷰는 노출되지 않는다.
     * <p>정렬 기준은 다음과 같다.
     * <ol>
     *     <li>리뷰를 작성한 장소의 카테고리가 내가 선호하는 음식 카테고리에 해당되는 경우</li>
     *     <li>최근 등록된 순서</li>
     * </ol>
     *
     * @param loginMemberId PK of login member
     * @param pageable      paging 정보
     * @return 조회된 리뷰 dtos
     */
    Slice<ReviewWithPlaceMarkedStatusDto> findReviewFeed(long loginMemberId, Pageable pageable);
}
