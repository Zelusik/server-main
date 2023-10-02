package com.zelusik.eatery.domain.review.service;

import com.zelusik.eatery.domain.bookmark.service.BookmarkQueryService;
import com.zelusik.eatery.domain.review.constant.ReviewEmbedOption;
import com.zelusik.eatery.domain.review.dto.ReviewWithPlaceMarkedStatusDto;
import com.zelusik.eatery.domain.review.entity.Review;
import com.zelusik.eatery.domain.review.exception.ReviewNotFoundException;
import com.zelusik.eatery.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.zelusik.eatery.domain.review.constant.ReviewEmbedOption.PLACE;
import static com.zelusik.eatery.domain.review.constant.ReviewEmbedOption.WRITER;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewQueryService {

    private final BookmarkQueryService bookmarkQueryService;
    private final ReviewRepository reviewRepository;

    /**
     * 주어진 PK에 해당하는 리뷰를 조회합니다.
     *
     * @param reviewId 조회하고자 하는 리뷰의 PK
     * @return 조회된 리뷰
     */
    public Review findById(Long reviewId) {
        return reviewRepository.findByIdAndDeletedAtNull(reviewId).orElseThrow(ReviewNotFoundException::new);
    }

    /**
     * 주어진 PK에 해당하는 리뷰를 단건 조회한다.
     *
     * @param memberId PK of member. 리뷰 내 장소의 북마크 여부 확인을 위해 필요하다.
     * @param reviewId 조회하고자 하는 리뷰의 PK
     * @return 조회된 리뷰 dto
     */
    public ReviewWithPlaceMarkedStatusDto findDtoById(Long memberId, Long reviewId) {
        Review review = findById(reviewId);
        boolean isMarkedPlace = bookmarkQueryService.isMarkedPlace(memberId, review.getPlace());
        return ReviewWithPlaceMarkedStatusDto.from(review, List.of(WRITER, PLACE), isMarkedPlace);
    }

    /**
     * <p>리뷰 목록 조회.
     * <p>장소에 대한 정보는 <code>null</code>로 처리하여 반환합니다. (query 최적화)
     * <p>정렬 기준은 최근 등록된 순서입니다.
     *
     * @param loginMemberId PK of login member
     * @param writerId      filter - 특정 회원이 작성한 리뷰만 조회
     * @param placeId       filter - 특정 가게에 대한 리뷰만 조회
     * @param embed         연관된 entity를 포함할지에 대한 여부
     * @param pageable      paging 정보
     * @return 조회된 리뷰 목록(Slice)
     */
    public Slice<ReviewWithPlaceMarkedStatusDto> findDtos(long loginMemberId, Long writerId, Long placeId, List<ReviewEmbedOption> embed, Pageable pageable) {
        return reviewRepository.findDtos(loginMemberId, writerId, placeId, embed, pageable);
    }

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
    public Slice<ReviewWithPlaceMarkedStatusDto> findReviewReed(long loginMemberId, Pageable pageable) {
        return reviewRepository.findReviewFeed(loginMemberId, pageable);
    }
}
