package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.domain.Review;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.dto.place.PlaceDto;
import com.zelusik.eatery.app.dto.place.request.PlaceCreateRequest;
import com.zelusik.eatery.app.dto.review.ReviewDtoWithMember;
import com.zelusik.eatery.app.dto.review.ReviewDtoWithMemberAndPlace;
import com.zelusik.eatery.app.dto.review.request.ReviewCreateRequest;
import com.zelusik.eatery.app.repository.ReviewRepository;
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
        PlaceCreateRequest placeCreateRequest = reviewRequest.getPlace();
        Place place = placeService.findOptEntityByKakaoPid(placeCreateRequest.getKakaoPid())
                .orElseGet(() -> placeService.create(placeCreateRequest));

        Member writer = memberService.findEntityById(writerId);

        ReviewDtoWithMemberAndPlace reviewDtoWithMemberAndPlace = reviewRequest.toDto(PlaceDto.from(place));
        Review review = reviewRepository.save(reviewDtoWithMemberAndPlace.toEntity(writer, place));
        reviewFileService.upload(review, files);

        return ReviewDtoWithMemberAndPlace.from(review);
    }

    /**
     * 특정 가게에 대헌 리뷰 목록(Slice) 조회.
     *
     * @param placeId  리뷰를 조회할 가게의 id(PK)
     * @param pageable paging 정보
     * @return 조회된 리뷰 목록(Slice)
     */
    public Slice<ReviewDtoWithMember> searchDtosByPlaceId(Long placeId, Pageable pageable) {
        // TODO: 현재 writer, place 정보를 사용하지 않음에도 ReviewDto가 해당 정보를 포함하고 있어 조회하게 된다. 최적화 필요.
        // => 작성자 누구인지 필요하다고 해서 작성자 정보는 유지할 예정
        // writer, place를 포함하지 않는 dto를 구현하여 적용하면 최적화 가능.
        return reviewRepository.findByPlace_Id(placeId, pageable).map(ReviewDtoWithMember::from);
    }

    /**
     * 리뷰 조회. 최신순 정렬
     *
     * @param pageable paging 정보
     * @return 조회된 리뷰 목록(slice)
     */
    public Slice<ReviewDtoWithMemberAndPlace> searchDtosOrderByCreatedAt(Pageable pageable) {
        return reviewRepository.findAll(pageable).map(ReviewDtoWithMemberAndPlace::from);
    }

    /**
     * 특정 회원이 작성한 리뷰 조회.
     *
     * @param writerId 작성자의 PK
     * @param pageable paging, sorting 정보
     * @return 조회된 리뷰 목록(slice)
     */
    public Slice<ReviewDtoWithMemberAndPlace> searchDtosByWriterId(Long writerId, Pageable pageable) {
        return reviewRepository.findByWriter_Id(writerId, pageable).map(ReviewDtoWithMemberAndPlace::from);
    }
}
