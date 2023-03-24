package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.app.domain.review.Review;
import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.domain.review.ReviewKeyword;
import com.zelusik.eatery.app.dto.place.PlaceDto;
import com.zelusik.eatery.app.dto.place.request.PlaceCreateRequest;
import com.zelusik.eatery.app.dto.review.ReviewDtoWithMember;
import com.zelusik.eatery.app.dto.review.ReviewDtoWithMemberAndPlace;
import com.zelusik.eatery.app.dto.review.request.ReviewCreateRequest;
import com.zelusik.eatery.app.repository.bookmark.BookmarkRepository;
import com.zelusik.eatery.app.repository.review.ReviewKeywordRepository;
import com.zelusik.eatery.app.repository.review.ReviewRepository;
import com.zelusik.eatery.global.exception.review.ReviewDeletePermissionDeniedException;
import com.zelusik.eatery.global.exception.review.ReviewNotFoundException;
import com.zelusik.eatery.global.exception.review.ReviewUpdatePermissionDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewService {

    private final ReviewFileService reviewFileService;
    private final MemberService memberService;
    private final PlaceService placeService;
    private final ReviewRepository reviewRepository;
    private final ReviewKeywordRepository reviewKeywordRepository;
    private final BookmarkRepository bookmarkRepository;

    /**
     * 리뷰를 생성합니다.
     *
     * @param writerId      리뷰를 생성하고자 하는 회원의 PK.
     * @param reviewRequest 생성할 리뷰의 정보. 여기에 장소 정보도 포함되어 있다.
     * @param files         리뷰와 함께 업로드 할 파일 목록
     * @return 생성된 리뷰 정보가 담긴 dto.
     */
    @Transactional
    public ReviewDtoWithMemberAndPlace create(Long writerId, ReviewCreateRequest reviewRequest, List<MultipartFile> files) {
        // 장소 조회 or 저장
        PlaceCreateRequest placeCreateRequest = reviewRequest.getPlace();
        Place place = placeService.findOptEntityByKakaoPid(placeCreateRequest.getKakaoPid())
                .orElseGet(() -> placeService.create(placeCreateRequest));

        Member writer = memberService.findEntityById(writerId);
        List<Long> markedPlaceIdList = bookmarkRepository.findAllMarkedPlaceId(writerId);

        // 리뷰 저장
        ReviewDtoWithMemberAndPlace reviewDtoWithMemberAndPlace = reviewRequest.toDto(PlaceDto.from(place, markedPlaceIdList));
        Review review = reviewDtoWithMemberAndPlace.toEntity(writer, place);
        reviewRepository.save(review);
        reviewDtoWithMemberAndPlace.keywords()
                .forEach(keyword -> {
                    ReviewKeyword reviewKeyword = ReviewKeyword.of(review, keyword);
                    review.getKeywords().add(reviewKeyword);
                    reviewKeywordRepository.save(reviewKeyword);
                });

        reviewFileService.upload(review, files);

        // 장소 top 3 keyword 설정
        List<ReviewKeywordValue> placeTop3Keywords = reviewKeywordRepository.searchTop3Keywords(place.getId());
        place.setTop3Keywords(placeTop3Keywords);

        return ReviewDtoWithMemberAndPlace.from(review, markedPlaceIdList);
    }

    /**
     * 주어진 PK에 해당하는 리뷰를 조회합니다.
     *
     * @param reviewId 조회하고자 하는 리뷰의 PK
     * @return 조회된 리뷰
     */
    private Review findEntityById(Long reviewId) {
        return reviewRepository.findByIdAndDeletedAtNull(reviewId)
                .orElseThrow(ReviewNotFoundException::new);
    }

    /**
     * 특정 가게에 대헌 리뷰 목록(Slice) 조회.
     *
     * @param placeId  리뷰를 조회할 가게의 id(PK)
     * @param pageable paging 정보
     * @return 조회된 리뷰 목록(Slice)
     */
    public Slice<ReviewDtoWithMember> searchDtosByPlaceId(Long placeId, Pageable pageable) {
        return reviewRepository.findByPlace_IdAndDeletedAtNull(placeId, pageable).map(ReviewDtoWithMember::from);
    }

    /**
     * 리뷰 조회. 최신순 정렬
     *
     * @param pageable paging 정보
     * @return 조회된 리뷰 목록(slice)
     */
    public Slice<ReviewDtoWithMemberAndPlace> searchDtosOrderByCreatedAt(Long memberId, Pageable pageable) {
        List<Long> markedPlaceIdList = bookmarkRepository.findAllMarkedPlaceId(memberId);

        return reviewRepository.findAllByDeletedAtNull(pageable)
                .map(review -> ReviewDtoWithMemberAndPlace.from(review, markedPlaceIdList));
    }

    /**
     * 특정 회원이 작성한 리뷰 조회.
     *
     * @param writerId 작성자의 PK
     * @param pageable paging, sorting 정보
     * @return 조회된 리뷰 목록(slice)
     */
    public Slice<ReviewDtoWithMemberAndPlace> searchDtosByWriterId(Long writerId, Pageable pageable) {
        List<Long> markedPlaceIdList = bookmarkRepository.findAllMarkedPlaceId(writerId);

        return reviewRepository.findByWriter_IdAndDeletedAtNull(writerId, pageable)
                .map(review -> ReviewDtoWithMemberAndPlace.from(review, markedPlaceIdList));
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
    public ReviewDtoWithMemberAndPlace update(Long memberId, Long reviewId, String content) {
        Review review = this.findEntityById(reviewId);
        validateReviewUpdatePermission(memberId, review);
        review.update(content);

        List<Long> markedPlaceIdList = bookmarkRepository.findAllMarkedPlaceId(memberId);

        return ReviewDtoWithMemberAndPlace.from(review, markedPlaceIdList);
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
        Member member = memberService.findEntityById(memberId);
        Review review = findEntityById(reviewId);

        validateReviewDeletePermission(member, review);

        reviewFileService.deleteAll(review.getReviewFiles());
        reviewRepository.delete(review);
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
