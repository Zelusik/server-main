package com.zelusik.eatery.unit.service;

import com.zelusik.eatery.constant.member.Gender;
import com.zelusik.eatery.constant.member.LoginType;
import com.zelusik.eatery.constant.member.RoleType;
import com.zelusik.eatery.constant.place.KakaoCategoryGroupCode;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.place.Address;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.place.PlaceCategory;
import com.zelusik.eatery.domain.place.Point;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.domain.review.ReviewImage;
import com.zelusik.eatery.domain.review.ReviewKeyword;
import com.zelusik.eatery.dto.review.request.MenuTagPointCreateRequest;
import com.zelusik.eatery.dto.review.request.ReviewImageCreateRequest;
import com.zelusik.eatery.dto.review.request.ReviewMenuTagCreateRequest;
import com.zelusik.eatery.repository.review.ReviewImageRepository;
import com.zelusik.eatery.service.FileService;
import com.zelusik.eatery.service.ReviewImageService;
import com.zelusik.eatery.service.S3ImageDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Review File Service")
@ExtendWith(MockitoExtension.class)
class ReviewImageServiceTest {

    @InjectMocks
    private ReviewImageService sut;

    @Mock
    private FileService fileService;
    @Mock
    private ReviewImageRepository reviewImageRepository;

    @DisplayName("이미지 파일들이 주어지면, 파일들을 업로드한다.")
    @Test
    void givenImageFiles_whenUploading_thenUploadFiles() {
        // given
        List<ReviewImageCreateRequest> images = List.of(createReviewImageCreateRequest());
        Member member = createMember(2L);
        Place place = createPlace(3L, "12345");
        Review review = createReview(1L, member, place);
        given(fileService.uploadImageWithResizing(any(MultipartFile.class), any(String.class))).willReturn(createS3ImageDto());
        given(reviewImageRepository.saveAll(any())).willReturn(List.of());

        // when
        sut.upload(review, images);

        // then
        then(fileService).should().uploadImageWithResizing(any(MultipartFile.class), any(String.class));
        then(reviewImageRepository).should().saveAll(any());
        then(fileService).shouldHaveNoMoreInteractions();
        then(reviewImageRepository).shouldHaveNoMoreInteractions();
    }

    private Review createReview(Long reviewId, Member member, Place place) {
        return createReview(reviewId, member, place, null, null);
    }

    private Review createReview(Long reviewId, Member member, Place place, List<ReviewKeyword> reviewKeywords, List<ReviewImage> reviewImages) {
        Review review = Review.of(
                reviewId,
                member,
                place,
                "자동 생성된 내용",
                "제출한 내용",
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
        if (reviewKeywords != null && !reviewKeywords.isEmpty()) {
            reviewKeywords.forEach(reviewKeyword -> review.getKeywords().add(reviewKeyword));
        }
        if (reviewImages != null && !reviewImages.isEmpty()) {
            reviewImages.forEach(reviewImage -> review.getReviewImages().add(reviewImage));
        }
        return review;
    }

    private ReviewMenuTagCreateRequest createReviewMenuTagCreateRequest(String content) {
        return new ReviewMenuTagCreateRequest(content, new MenuTagPointCreateRequest("10.0", "50.0"));
    }

    private ReviewImageCreateRequest createReviewImageCreateRequest() {
        return new ReviewImageCreateRequest(
                createMockMultipartFile(),
                List.of(
                        createReviewMenuTagCreateRequest("치킨"),
                        createReviewMenuTagCreateRequest("피자")
                )
        );
    }

    private Member createMember(long memberId) {
        return createMember(memberId, Set.of(RoleType.USER));
    }

    private Member createMember(long memberId, Set<RoleType> roleTypes) {
        return Member.of(
                memberId,
                "profile image url",
                "profile thunmbnail image url",
                "social user id" + memberId,
                LoginType.KAKAO,
                roleTypes,
                "email",
                "nickname",
                LocalDate.of(2000, 1, 1),
                20,
                Gender.MALE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private Place createPlace(long id, String kakaoPid) {
        return Place.of(
                id,
                List.of(ReviewKeywordValue.FRESH),
                kakaoPid,
                "place name",
                "page url",
                KakaoCategoryGroupCode.FD6,
                new PlaceCategory("한식", "냉면", null),
                null,
                new Address("sido", "sgg", "lot number address", "road address"),
                null,
                new Point("37.5595073462493", "126.921462488105"),
                "",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private S3ImageDto createS3ImageDto() {
        return S3ImageDto.of(
                "originalFileName",
                "storedFileName",
                "url",
                "thumbnailStoredFileName",
                "thumbnailUrl"
        );
    }

    private MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile(
                "test",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test".getBytes()
        );
    }
}