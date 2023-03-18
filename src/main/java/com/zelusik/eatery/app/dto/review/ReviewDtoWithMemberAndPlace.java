package com.zelusik.eatery.app.dto.review;

import com.zelusik.eatery.app.domain.Member;
import com.zelusik.eatery.app.domain.Review;
import com.zelusik.eatery.app.constant.review.ReviewKeyword;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.dto.member.MemberDto;
import com.zelusik.eatery.app.dto.place.PlaceDto;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewDtoWithMemberAndPlace(
        Long id,
        MemberDto writerDto,
        PlaceDto placeDto,
        List<ReviewKeyword> keywords,
        String autoCreatedContent,
        String content,
        List<ReviewFileDto> reviewFileDtos,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {

    public static ReviewDtoWithMemberAndPlace of(PlaceDto placeDto, List<ReviewKeyword> keywords, String autoCreatedContent, String content) {
        return of(null, null, placeDto, keywords, autoCreatedContent, content, null, null, null, null);
    }

    public static ReviewDtoWithMemberAndPlace of(Long id, MemberDto writerDto, PlaceDto place, List<ReviewKeyword> keywords, String autoCreatedContent, String content, List<ReviewFileDto> reviewFileDtos, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new ReviewDtoWithMemberAndPlace(id, writerDto, place, keywords, autoCreatedContent, content, reviewFileDtos, createdAt, updatedAt, deletedAt);
    }

    public static ReviewDtoWithMemberAndPlace from(Review entity) {
        return of(
                entity.getId(),
                MemberDto.from(entity.getWriter()),
                PlaceDto.from(entity.getPlace()),
                entity.getKeywords(),
                entity.getAutoCreatedContent(),
                entity.getContent(),
                entity.getReviewFiles().stream()
                        .map(ReviewFileDto::from)
                        .toList(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    public Review toEntity(Member writer, Place place) {
        return Review.of(
                writer,
                place,
                this.keywords(),
                this.autoCreatedContent(),
                this.content()
        );
    }
}
