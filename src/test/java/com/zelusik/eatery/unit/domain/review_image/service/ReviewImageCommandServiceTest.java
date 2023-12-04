package com.zelusik.eatery.unit.domain.review_image.service;

import com.zelusik.eatery.domain.member.constant.Gender;
import com.zelusik.eatery.domain.member.constant.LoginType;
import com.zelusik.eatery.domain.member.constant.RoleType;
import com.zelusik.eatery.domain.member.entity.Member;
import com.zelusik.eatery.domain.place.constant.KakaoCategoryGroupCode;
import com.zelusik.eatery.domain.place.entity.Address;
import com.zelusik.eatery.domain.place.entity.Place;
import com.zelusik.eatery.domain.place.entity.PlaceCategory;
import com.zelusik.eatery.domain.place.entity.Point;
import com.zelusik.eatery.domain.review.constant.ReviewKeywordValue;
import com.zelusik.eatery.domain.review.entity.Review;
import com.zelusik.eatery.domain.review_image.dto.request.ReviewImageCreateRequest;
import com.zelusik.eatery.domain.review_image.entity.ReviewImage;
import com.zelusik.eatery.domain.review_image.repository.ReviewImageRepository;
import com.zelusik.eatery.domain.review_image.service.ReviewImageCommandService;
import com.zelusik.eatery.domain.review_image_menu_tag.dto.request.MenuTagPointCreateRequest;
import com.zelusik.eatery.domain.review_image_menu_tag.dto.request.ReviewMenuTagCreateRequest;
import com.zelusik.eatery.domain.review_keyword.entity.ReviewKeyword;
import com.zelusik.eatery.global.file.dto.S3ImageDto;
import com.zelusik.eatery.global.file.service.S3FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.zelusik.eatery.domain.review_image.service.ReviewImageCommandService.AWS_S3_DIR_PATH;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Unit] Service(Command) - Review image")
@ExtendWith(MockitoExtension.class)
class ReviewImageCommandServiceTest {

    @InjectMocks
    private ReviewImageCommandService sut;

    @Mock
    private S3FileService s3FileService;
    @Mock
    private ReviewImageRepository reviewImageRepository;

    @DisplayName("이미지 파일들이 주어지면, 파일들을 업로드한다.")
    @Test
    void givenImageFiles_whenUploading_thenUploadFiles() {
        // given
        List<ReviewImageCreateRequest> reviewImageCreateRequests = List.of(createReviewImageCreateRequest());
        Member member = createMember(2L);
        Place place = createPlace(3L, "12345");
        Review review = createReview(1L, member, place);
        AsyncResult<S3ImageDto> expectedResultOfAsyncImageUpload = new AsyncResult<>(createS3ImageDto());
        List<ReviewImage> expectedResultOfReviewImagesSave = List.of();
        given(s3FileService.asyncUploadImageWithResizing(any(MultipartFile.class), eq(AWS_S3_DIR_PATH))).willReturn(expectedResultOfAsyncImageUpload);
        given(reviewImageRepository.saveAll(anyList())).willReturn(expectedResultOfReviewImagesSave);

        // when
        sut.uploadReviewImages(review, reviewImageCreateRequests);

        // then
        then(s3FileService).should().asyncUploadImageWithResizing(any(MultipartFile.class), eq(AWS_S3_DIR_PATH));
        then(reviewImageRepository).should().saveAll(any());
        verifyEveryMocksShouldHaveNoMoreInteractions();
    }

    private void verifyEveryMocksShouldHaveNoMoreInteractions() {
        then(s3FileService).shouldHaveNoMoreInteractions();
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
        return new S3ImageDto(
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