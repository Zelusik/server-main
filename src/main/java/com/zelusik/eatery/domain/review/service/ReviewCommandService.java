package com.zelusik.eatery.domain.review.service;

import com.zelusik.eatery.domain.bookmark.service.BookmarkQueryService;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.member.service.MemberQueryService;
import com.zelusik.eatery.domain.place.dto.PlaceDto;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.service.PlaceCommandService;
import com.zelusik.eatery.domain.place.service.PlaceQueryService;
import com.zelusik.eatery.domain.review.dto.ReviewDto;
import com.zelusik.eatery.domain.review.dto.ReviewWithPlaceMarkedStatusDto;
import com.zelusik.eatery.domain.review.dto.request.ReviewCreateRequest;
import com.zelusik.eatery.domain.review.entity.Review;
import com.zelusik.eatery.domain.review.exception.ReviewDeletePermissionDeniedException;
import com.zelusik.eatery.domain.review.exception.ReviewUpdatePermissionDeniedException;
import com.zelusik.eatery.domain.review.repository.ReviewRepository;
import com.zelusik.eatery.domain.review_image.dto.request.ReviewImageCreateRequest;
import com.zelusik.eatery.domain.review_image.entity.ReviewImage;
import com.zelusik.eatery.domain.review_image.service.ReviewImageCommandService;
import com.zelusik.eatery.domain.review_image_menu_tag.dto.request.ReviewMenuTagCreateRequest;
import com.zelusik.eatery.domain.review_image_menu_tag.entity.ReviewImageMenuTag;
import com.zelusik.eatery.domain.review_image_menu_tag.repository.ReviewImageMenuTagRepository;
import com.zelusik.eatery.domain.review_keyword.entity.ReviewKeyword;
import com.zelusik.eatery.domain.review_keyword.repository.ReviewKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.zelusik.eatery.domain.review.constant.ReviewEmbedOption.PLACE;
import static com.zelusik.eatery.domain.review.constant.ReviewEmbedOption.WRITER;

@RequiredArgsConstructor
@Transactional
@Service
public class ReviewCommandService {

    private final ReviewQueryService reviewQueryService;
    private final ReviewImageCommandService reviewImageCommandService;
    private final MemberQueryService memberQueryService;
    private final PlaceCommandService placeCommandService;
    private final PlaceQueryService placeQueryService;
    private final BookmarkQueryService bookmarkQueryService;
    private final ReviewRepository reviewRepository;
    private final ReviewKeywordRepository reviewKeywordRepository;
    private final ReviewImageMenuTagRepository reviewImageMenuTagRepository;

    /**
     * 리뷰를 생성합니다.
     *
     * @param writerId            리뷰를 생성하고자 하는 회원의 PK.
     * @param reviewCreateRequest 생성할 리뷰의 정보. 여기에 장소 정보도 포함되어 있다.
     * @return 생성된 리뷰 정보가 담긴 dto.
     */
    public ReviewWithPlaceMarkedStatusDto create(
            @NotNull Long writerId,
            @NotNull ReviewCreateRequest reviewCreateRequest
    ) {
        List<ReviewImageCreateRequest> imageRequests = reviewCreateRequest.getImages();
        Place place = placeQueryService.findById(reviewCreateRequest.getPlaceId());
        Member writer = memberQueryService.findById(writerId);

        // 리뷰 저장
        ReviewDto newReviewDto = ReviewDto.createNewReviewDto(
                PlaceDto.from(place),
                reviewCreateRequest.getKeywords(),
                reviewCreateRequest.getAutoCreatedContent(),
                reviewCreateRequest.getContent()
        );
        Review review = reviewRepository.save(newReviewDto.toEntity(writer, place));

        // 리뷰 키워드 저장
        reviewCreateRequest.getKeywords().forEach(keyword -> {
            ReviewKeyword reviewKeyword = ReviewKeyword.createNewReviewKeyword(review, keyword);
            review.getKeywords().add(reviewKeyword);
            reviewKeywordRepository.save(reviewKeyword);
        });

        // 리뷰 이미지 저장 및 업로드
        List<ReviewImage> reviewImages = reviewImageCommandService.uploadReviewImages(review, imageRequests);
        review.getReviewImages().addAll(reviewImages);

        // Manu tag 저장
        for (int i = 0; i < imageRequests.size(); i++) {
            List<ReviewMenuTagCreateRequest> menuTags = imageRequests.get(i).getMenuTags();
            ReviewImage reviewImage = review.getReviewImages().get(i);
            saveReviewImageMenuTags(menuTags, reviewImage);
        }

        // 장소 top 3 keyword 갱신
        placeCommandService.renewTop3Keywords(place);

        boolean placeMarkedStatus = bookmarkQueryService.isMarkedPlace(writerId, place);

        return ReviewWithPlaceMarkedStatusDto.from(review, List.of(WRITER, PLACE), placeMarkedStatus);
    }

    /**
     * 리뷰를 수정합니다.
     *
     * @param memberId 리뷰를 수정하고자 하는 회원(로그인 회원)의 PK
     * @param reviewId 수정할 리뷰의 PK
     * @param content  수정하고자 하는 리뷰 내용
     * @throws ReviewUpdatePermissionDeniedException 리뷰 수정 권한이 없는 경우
     */
    public ReviewWithPlaceMarkedStatusDto update(Long memberId, Long reviewId, @Nullable String content) {
        Review review = reviewQueryService.findById(reviewId);
        validateReviewUpdatePermission(memberId, review);
        review.update(content);

        boolean placeMarkedStatus = bookmarkQueryService.isMarkedPlace(memberId, review.getPlace());
        return ReviewWithPlaceMarkedStatusDto.from(review, List.of(WRITER, PLACE), placeMarkedStatus);
    }

    /**
     * 리뷰를 삭제합니다.
     *
     * @param memberId 리뷰룰 삭제하려는 회원(로그인 회원)의 PK.
     * @param reviewId 삭제하려는 리뷰의 PK
     * @throws ReviewDeletePermissionDeniedException 리뷰 삭제 권한이 없는 경우
     */
    public void delete(Long memberId, Long reviewId) {
        Member member = memberQueryService.findById(memberId);
        Review review = reviewQueryService.findById(reviewId);

        validateReviewDeletePermission(member, review);

        reviewImageCommandService.softDeleteAll(review.getReviewImages());
        reviewKeywordRepository.deleteAll(review.getKeywords());
        softDelete(review);

        placeCommandService.renewTop3Keywords(review.getPlace());
    }

    /**
     * 주어진 menu tag create requests에 대해 menu tag entity를 생성 및 저장한다.
     *
     * @param menuTagCreateReqs 생성 및 저장할 menu tag 정보가 담긴 dto
     * @param reviewImage       menu tag가 태그된 review image
     */
    private void saveReviewImageMenuTags(
            @NotNull List<ReviewMenuTagCreateRequest> menuTagCreateReqs,
            @NotNull ReviewImage reviewImage
    ) {
        if (menuTagCreateReqs == null || menuTagCreateReqs.isEmpty()) {
            return;
        }
        List<ReviewImageMenuTag> menuTags = menuTagCreateReqs.stream()
                .map(reviewMenuTagReq -> reviewMenuTagReq.toDto().toEntity(reviewImage))
                .toList();
        reviewImage.getMenuTags().addAll(menuTags);
        reviewImageMenuTagRepository.saveAll(reviewImage.getMenuTags());
    }

    /**
     * 리뷰 수정 권한이 있는지 검증한다.
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
