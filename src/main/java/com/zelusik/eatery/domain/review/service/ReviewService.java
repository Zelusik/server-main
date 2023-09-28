package com.zelusik.eatery.domain.review.service;

import com.zelusik.eatery.domain.bookmark.service.BookmarkQueryService;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.member.service.MemberQueryService;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.service.PlaceService;
import com.zelusik.eatery.domain.review.constant.ReviewEmbedOption;
import com.zelusik.eatery.domain.review.dto.ReviewDto;
import com.zelusik.eatery.domain.review.dto.request.ReviewCreateRequest;
import com.zelusik.eatery.domain.review.entity.Review;
import com.zelusik.eatery.domain.review.exception.ReviewDeletePermissionDeniedException;
import com.zelusik.eatery.domain.review.exception.ReviewNotFoundException;
import com.zelusik.eatery.domain.review.exception.ReviewUpdatePermissionDeniedException;
import com.zelusik.eatery.domain.review.repository.ReviewRepository;
import com.zelusik.eatery.domain.review_image.dto.request.ReviewImageCreateRequest;
import com.zelusik.eatery.domain.review_image.entity.ReviewImage;
import com.zelusik.eatery.domain.review_image.service.ReviewImageService;
import com.zelusik.eatery.domain.review_image_menu_tag.entity.ReviewImageMenuTag;
import com.zelusik.eatery.domain.review_image_menu_tag.repository.ReviewImageMenuTagRepository;
import com.zelusik.eatery.domain.review_keyword.entity.ReviewKeyword;
import com.zelusik.eatery.domain.review_keyword.repository.ReviewKeywordRepository;
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
    private final MemberQueryService memberQueryService;
    private final PlaceService placeService;
    private final BookmarkQueryService bookmarkQueryService;
    private final ReviewRepository reviewRepository;
    private final ReviewKeywordRepository reviewKeywordRepository;
    private final ReviewImageMenuTagRepository reviewImageMenuTagRepository;

    /**
     * 리뷰를 생성합니다.
     *
     * @param writerId      리뷰를 생성하고자 하는 회원의 PK.
     * @param reviewRequest 생성할 리뷰의 정보. 여기에 장소 정보도 포함되어 있다.
     * @return 생성된 리뷰 정보가 담긴 dto.
     */
    @Transactional
    public ReviewDto create(Long writerId, ReviewCreateRequest reviewRequest) {
        List<ReviewImageCreateRequest> images = reviewRequest.getImages();
        Place place = placeService.findById(reviewRequest.getPlaceId());
        Member writer = memberQueryService.findById(writerId);
        boolean placeMarkedStatus = bookmarkQueryService.isMarkedPlace(writerId, place);

        // 리뷰 저장
        ReviewDto reviewDto = reviewRequest.toDto(PlaceDto.from(place, placeMarkedStatus));
        Review review = reviewRepository.save(reviewDto.toEntity(writer, place));

        // 리뷰 키워드 저장
        reviewDto.getKeywords().forEach(keyword -> {
            ReviewKeyword reviewKeyword = ReviewKeyword.of(review, keyword);
            review.getKeywords().add(reviewKeyword);
            reviewKeywordRepository.save(reviewKeyword);
        });

        // 리뷰 이미지 저장 및 업로드
        List<ReviewImage> reviewImages = reviewImageService.upload(review, images);
        review.getReviewImages().addAll(reviewImages);

        // 메뉴 태그 저장
        for (int i = 0; i < images.size(); i++) {
            ReviewImageCreateRequest reviewImageReq = images.get(i);
            ReviewImage reviewImage = review.getReviewImages().get(i);

            if (reviewImageReq.getMenuTags() == null || reviewImageReq.getMenuTags().isEmpty()) {
                continue;
            }

            List<ReviewImageMenuTag> menuTags = reviewImageReq.getMenuTags().stream()
                    .map(reviewMenuTagReq -> reviewMenuTagReq.toDto().toEntity(reviewImage))
                    .toList();
            reviewImage.getMenuTags().addAll(menuTags);
            reviewImageMenuTagRepository.saveAll(reviewImage.getMenuTags());
        }

        // 장소 top 3 keyword 갱신
        placeService.renewTop3Keywords(place);

        return ReviewDto.from(review, placeMarkedStatus);
    }

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
    public ReviewDto findDtoById(Long memberId, Long reviewId) {
        Review review = findById(reviewId);
        boolean isMarkedPlace = bookmarkQueryService.isMarkedPlace(memberId, review.getPlace());
        return ReviewDto.from(review, isMarkedPlace);
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
    public Slice<ReviewDto> findDtos(long loginMemberId, Long writerId, Long placeId, List<ReviewEmbedOption> embed, Pageable pageable) {
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
    public Slice<ReviewDto> findReviewReed(long loginMemberId, Pageable pageable) {
        return reviewRepository.findReviewFeed(loginMemberId, pageable);
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
    public ReviewDto update(Long memberId, Long reviewId, @Nullable String content) {
        Review review = findById(reviewId);
        validateReviewUpdatePermission(memberId, review);
        review.update(content);

        return ReviewDto.from(review, bookmarkQueryService.isMarkedPlace(memberId, review.getPlace()));
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
     * 리뷰를 삭제합니다.
     *
     * @param memberId 리뷰룰 삭제하려는 회원(로그인 회원)의 PK.
     * @param reviewId 삭제하려는 리뷰의 PK
     * @throws ReviewDeletePermissionDeniedException 리뷰 삭제 권한이 없는 경우
     */
    @Transactional
    public void delete(Long memberId, Long reviewId) {
        Member member = memberQueryService.findById(memberId);
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
