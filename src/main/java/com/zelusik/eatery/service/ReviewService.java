package com.zelusik.eatery.service;

import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.domain.review.ReviewKeyword;
import com.zelusik.eatery.dto.ImageDto;
import com.zelusik.eatery.dto.place.PlaceDto;
import com.zelusik.eatery.dto.place.request.PlaceCreateRequest;
import com.zelusik.eatery.dto.review.ReviewDtoWithMember;
import com.zelusik.eatery.dto.review.ReviewDtoWithMemberAndPlace;
import com.zelusik.eatery.dto.review.request.ReviewCreateRequest;
import com.zelusik.eatery.repository.review.ReviewKeywordRepository;
import com.zelusik.eatery.repository.review.ReviewRepository;
import com.zelusik.eatery.exception.review.ReviewDeletePermissionDeniedException;
import com.zelusik.eatery.exception.review.ReviewNotFoundException;
import com.zelusik.eatery.exception.review.ReviewUpdatePermissionDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewService {

    private final ReviewImageService reviewImageService;
    private final MemberService memberService;
    private final PlaceService placeService;
    private final ReviewRepository reviewRepository;
    private final ReviewKeywordRepository reviewKeywordRepository;
    private final BookmarkService bookmarkService;

    /**
     * 리뷰를 생성합니다.
     *
     * @param writerId      리뷰를 생성하고자 하는 회원의 PK.
     * @param reviewRequest 생성할 리뷰의 정보. 여기에 장소 정보도 포함되어 있다.
     * @param images        리뷰와 함께 업로드 할 파일 목록
     * @return 생성된 리뷰 정보가 담긴 dto.
     */
    @Transactional
    public ReviewDtoWithMemberAndPlace create(Long writerId, ReviewCreateRequest reviewRequest, List<ImageDto> images) {
        // 장소 조회 or 저장
        PlaceCreateRequest placeCreateRequest = reviewRequest.getPlace();
        Place place = placeService.findOptByKakaoPid(placeCreateRequest.getKakaoPid())
                .orElseGet(() -> {
                    Long createdPlaceId = placeService.create(writerId, placeCreateRequest).getId();
                    return placeService.findById(createdPlaceId);
                });

        Member writer = memberService.findById(writerId);

        boolean isMarkedPlace = bookmarkService.isMarkedPlace(writerId, place);

        // 리뷰 저장
        ReviewDtoWithMemberAndPlace reviewDtoWithMemberAndPlace = reviewRequest.toDto(PlaceDto.from(place, isMarkedPlace));
        Review review = reviewDtoWithMemberAndPlace.toEntity(writer, place);
        reviewRepository.save(review);
        reviewDtoWithMemberAndPlace.getKeywords()
                .forEach(keyword -> {
                    ReviewKeyword reviewKeyword = ReviewKeyword.of(review, keyword);
                    review.getKeywords().add(reviewKeyword);
                    reviewKeywordRepository.save(reviewKeyword);
                });

        reviewImageService.upload(review, images);

        // 장소 top 3 keyword 설정
        placeService.renewTop3Keywords(place);

        return ReviewDtoWithMemberAndPlace.from(review, isMarkedPlace);
    }

    /**
     * 주어진 PK에 해당하는 리뷰를 조회합니다.
     *
     * @param reviewId 조회하고자 하는 리뷰의 PK
     * @return 조회된 리뷰
     */
    private Review findById(Long reviewId) {
        return reviewRepository.findByIdAndDeletedAtNull(reviewId)
                .orElseThrow(ReviewNotFoundException::new);
    }

    /**
     * 전체 리뷰 조회. 최신순 정렬
     *
     * @param pageable paging 정보
     * @return 조회된 리뷰 목록(slice)
     */
    public Slice<ReviewDtoWithMemberAndPlace> findDtosOrderByCreatedAt(Long memberId, Pageable pageable) {
        return reviewRepository.findAllByDeletedAtNull(pageable)
                .map(review -> ReviewDtoWithMemberAndPlace.from(review, bookmarkService.isMarkedPlace(memberId, review.getPlace())));
    }

    /**
     * 특정 가게에 대헌 리뷰 목록(Slice) 조회.
     *
     * @param placeId  리뷰를 조회할 가게의 id(PK)
     * @param pageable paging 정보
     * @return 조회된 리뷰 목록(Slice)
     */
    public Slice<ReviewDtoWithMember> findDtosByPlaceId(Long placeId, Pageable pageable) {
        return reviewRepository.findByPlace_IdAndDeletedAtNull(placeId, pageable).map(ReviewDtoWithMember::from);
    }

    /**
     * 특정 회원이 작성한 리뷰 조회.
     *
     * @param writerId 작성자의 PK
     * @param pageable paging, sorting 정보
     * @return 조회된 리뷰 목록(slice)
     */
    public Slice<ReviewDtoWithMemberAndPlace> findDtosByWriterId(Long writerId, Pageable pageable) {
        return reviewRepository.findByWriter_IdAndDeletedAtNull(writerId, pageable)
                .map(review -> ReviewDtoWithMemberAndPlace.from(review, bookmarkService.isMarkedPlace(writerId, review.getPlace())));
    }

    /**
     * 리뷰를 수정합니다.
     *
     * @param memberId 리뷰를 수정하고자 하는 회원(로그인 회원)의 PK
     * @param reviewId 수정할 리뷰의 PK
     * @param content  수정하고자 하는 리뷰 내용
     * @throws ReviewUpdatePermissionDeniedException 리뷰 수정 권한이 없는 경우
     */
    @Transactional
    public ReviewDtoWithMemberAndPlace update(Long memberId, Long reviewId, @Nullable String content) {
        Review review = findById(reviewId);
        validateReviewUpdatePermission(memberId, review);
        review.update(content);

        return ReviewDtoWithMemberAndPlace.from(review, bookmarkService.isMarkedPlace(memberId, review.getPlace()));
    }

    /**
     * 리뷰를 삭제합니다.
     *
     * @param memberId 리뷰룰 삭제하려는 회원(로그인 회원)의 PK.
     * @param reviewId 삭제하려는 리뷰의 PK
     * @throws ReviewDeletePermissionDeniedException 리뷰 삭제 권한이 없는 경우
     */
    @Transactional
    public void delete(Long memberId, Long reviewId) {
        Member member = memberService.findById(memberId);
        Review review = findById(reviewId);

        validateReviewDeletePermission(member, review);

        reviewImageService.softDeleteAll(review.getReviewImages());
        reviewKeywordRepository.deleteAll(review.getKeywords());
        softDelete(review);

        placeService.renewTop3Keywords(review.getPlace());
    }

    private void softDelete(Review review) {
        review.softDelete();
        reviewRepository.flush();
    }

    /**
     * 리뷰 슈정 권한이 있는지 검증한다.
     *
     * @param memberId 리뷰를 수정하고자 하는 회원(로그인 회원)
     * @param review   수정할 리뷰
     * @throws ReviewUpdatePermissionDeniedException 리뷰 수정 권한이 없는 경우
     */
    private void validateReviewUpdatePermission(Long memberId, Review review) {
        if (!review.getWriter().getId().equals(memberId)) {
            throw new ReviewUpdatePermissionDeniedException();
        }
    }

    /**
     * 리뷰 삭제 권한이 있는지 검증한다.
     *
     * @param member 리뷰를 삭제하고자 하는 회원
     * @param review 삭제할 리뷰
     * @throws ReviewDeletePermissionDeniedException 리뷰를 삭제할 권한이 없는 회원인 경우
     */
    private void validateReviewDeletePermission(Member member, Review review) {
        if (!review.getWriter().getId().equals(member.getId())) {
            throw new ReviewDeletePermissionDeniedException();
        }
    }
}
