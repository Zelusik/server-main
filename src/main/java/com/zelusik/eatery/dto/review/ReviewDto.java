package com.zelusik.eatery.dto.review;

import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.domain.review.Review;
import com.zelusik.eatery.domain.review.ReviewKeyword;
import com.zelusik.eatery.dto.member.MemberDto;
import com.zelusik.eatery.dto.place.PlaceDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReviewDto {

    private Long id;
    private MemberDto writer;
    private PlaceDto place;
    private List<ReviewKeywordValue> keywords;
    private String autoCreatedContent;
    private String content;
    private List<ReviewImageDto> reviewImageDtos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @NonNull
    public static ReviewDto of(PlaceDto place, List<ReviewKeywordValue> keywords, String autoCreatedContent, String content) {
        return of(null, null, place, keywords, autoCreatedContent, content, null, null, null, null);
    }

    @NonNull
    public static ReviewDto of(Long id, MemberDto writer, PlaceDto place, List<ReviewKeywordValue> keywords, String autoCreatedContent, String content, List<ReviewImageDto> reviewImageDtos, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new ReviewDto(id, writer, place, keywords, autoCreatedContent, content, reviewImageDtos, createdAt, updatedAt, deletedAt);
    }

    @NonNull
    public static ReviewDto from(@NonNull Review entity, @NonNull Boolean isMarkedPlace) {
        return of(
                entity.getId(),
                MemberDto.from(entity.getWriter()),
                PlaceDto.from(entity.getPlace(), isMarkedPlace),
                entity.getKeywords().stream()
                        .map(ReviewKeyword::getKeyword)
                        .toList(),
                entity.getAutoCreatedContent(),
                entity.getContent(),
                entity.getReviewImages().stream()
                        .map(ReviewImageDto::from)
                        .toList(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    @NonNull
    public static ReviewDto fromWithoutPlace(@NonNull Review entity) {
        return of(
                entity.getId(),
                MemberDto.from(entity.getWriter()),
                null,
                entity.getKeywords().stream()
                        .map(ReviewKeyword::getKeyword)
                        .toList(),
                entity.getAutoCreatedContent(),
                entity.getContent(),
                entity.getReviewImages().stream()
                        .map(ReviewImageDto::from)
                        .toList(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    @NonNull
    public Review toEntity(Member writer, Place place) {
        return Review.of(
                writer,
                place,
                this.getAutoCreatedContent(),
                this.getContent()
        );
    }
}
